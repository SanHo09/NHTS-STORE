/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.nhom4.nhtsstore.ui.pointOfSale;

import com.nhom4.nhtsstore.entities.*;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.enums.OrderStatus;
import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.services.*;
import com.nhom4.nhtsstore.services.impl.QRDisplayService;
import com.nhom4.nhtsstore.services.IInvoiceExportService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import com.nhom4.nhtsstore.viewmodel.cart.CartItemVm;
import com.nhom4.nhtsstore.viewmodel.cart.CartVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import javax.swing.Timer;

/**
 * @author Sang
 */
@Scope("prototype")
@Component
@Slf4j
public class InvoicePanel extends javax.swing.JPanel implements RoutablePanel {

    /**
     * Creates new form InvoicePanel
     */

    private final ApplicationState applicationState;
    private final IOrderService orderService;
    private final IPaymentService paymentService;
    private final IInvoiceService invoiceService;
    private final ICustomerService customerService;
    private final IProductService productService;
    private final NavigationService navigationService;
    private final QRDisplayService qrDisplayService;
    private final IInvoiceExportService invoiceExportService;

    private CartVm cart;
    private Order order;
    private UserSessionVm currentUser;
    private List<OrderDetail> orderDetails;
    private Timer statusCheckTimer;
    private String currentAppTransId;
    private PaymentMethod selectedPaymentMethod = PaymentMethod.CASH;
    private boolean isProcessingPayment = false;

    public InvoicePanel(ApplicationState applicationState, IOrderService orderService, IPaymentService paymentService, IInvoiceService invoiceService, ICustomerService customerService, IProductService productService, NavigationService navigationService, QRDisplayService qrDisplayService, IInvoiceExportService invoiceExportService) {
        this.applicationState = applicationState;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
        this.customerService = customerService;
        this.productService = productService;
        this.navigationService = navigationService;
        this.qrDisplayService = qrDisplayService;
        this.invoiceExportService = invoiceExportService;
        initComponents();
        setupPaymentMethodComboBox();
        setupDocumentListeners();
    }

    private void setupDocumentListeners() {
        javax.swing.event.DocumentListener documentListener = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                loadInvoice();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                loadInvoice();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                loadInvoice();
            }
        };

        txtCustomerName.getDocument().addDocumentListener(documentListener);
        txtEmail.getDocument().addDocumentListener(documentListener);
        txtPhoneNumber.getDocument().addDocumentListener(documentListener);
        txtAddress.getDocument().addDocumentListener(documentListener);
    }

    @Override
    public void onNavigate(RouteParams params) {
        currentUser = applicationState.getCurrentUser();
        cart = applicationState.getCart();
        loadInvoice();
        order = createOrderFromCart();

    }
    private void setupPaymentMethodComboBox() {
        DefaultComboBoxModel<PaymentMethod> model = new DefaultComboBoxModel<>();
        model.addElement(PaymentMethod.CASH);
        model.addElement(PaymentMethod.ZALOPAY);
        cmbPaymentMethod.setModel(model);

        cmbPaymentMethod.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                PaymentMethod newMethod = (PaymentMethod) e.getItem();
                if (newMethod != selectedPaymentMethod) {
                    // If there's an ongoing payment, cancel it
                    if (isProcessingPayment) {
                        cancelPayment();
                    }
                    selectedPaymentMethod = newMethod;
                }
            }
        });

    }

    public void loadInvoice() {
        StringBuilder invoiceText = new StringBuilder();
        invoiceText.append("*************************************************************Invoice Detail**************************************************************\n");
        invoiceText.append("****************************************************************************************************************************************\n");
        String customerName = txtCustomerName.getText().trim();
        String email = txtEmail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String address = txtAddress.getText().trim();

        if (!customerName.isEmpty() || !email.isEmpty() || !phoneNumber.isEmpty() || !address.isEmpty()) {
            invoiceText.append("Customer Information:\n");
            if (!customerName.isEmpty()) {
                invoiceText.append("Name: ").append(customerName).append("\n");
            }
            if (!email.isEmpty()) {
                invoiceText.append("Email: ").append(email).append("\n");
            }
            if (!phoneNumber.isEmpty()) {
                invoiceText.append("Phone: ").append(phoneNumber).append("\n");
            }
            if (!address.isEmpty()) {
                invoiceText.append("Address: ").append(address).append("\n");
            }
            invoiceText.append("--------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        }
        invoiceText.append("Item Name:\t\t\t\t").append("Number \t\t\t").append("Price($)\n");
        cart.getItems().forEach(item -> {
            invoiceText.append(item.getProductName())
                    .append("\t\t\t")
                    .append(item.getQuantity())
                    .append("\t\t\t")
                    .append(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .append("\n");
        });

        txtOrder.setText(invoiceText.toString());
        BigDecimal totalPrice = cart.getTotalAmount();
        lblTotalPrice.setText("Total sale price: " + totalPrice + " $");
        lblTranportFee.setText("Transport fee: " + 0 + "$");
        lblTotal.setText("Total: " + totalPrice + " $");
    }

    private List<InvoiceDetail> mapCartItemsToInvoiceDetails(List<CartItemVm> items, Invoice invoice) {

        List<InvoiceDetail> invoiceDetails = new ArrayList<>();
        for (CartItemVm item : items) {
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(invoice);
            Product product = Product.builder().id(item.getProductId()).build();
            invoiceDetail.setProduct(product);
            invoiceDetail.setQuantity(item.getQuantity());
            invoiceDetail.setUnitPrice(item.getPrice());
            invoiceDetail.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            invoiceDetails.add(invoiceDetail);
        }
        return invoiceDetails;
    }

    private Customer getCustomer() {
        String email = txtEmail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();

        // Check if customer already exists by email or phone number
        if (!email.isEmpty() || !phoneNumber.isEmpty()) {
            Customer existingCustomer = customerService.findByEmailOrPhoneNumber(email, phoneNumber);
            if (existingCustomer != null) {
                // Update existing customer with any new information
                existingCustomer.setName(txtCustomerName.getText().trim());
                if (!email.isEmpty()) {
                    existingCustomer.setEmail(email);
                }
                if (!phoneNumber.isEmpty()) {
                    existingCustomer.setPhoneNumber(phoneNumber);
                }
                existingCustomer.setAddress(txtAddress.getText().trim());
                return existingCustomer;
            }
        }

        // Create new customer if no existing customer found

        Customer customer = new Customer();
        customer.setName(txtCustomerName.getText().trim());
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(txtAddress.getText().trim());
        return customer;
    }

    private void updateInventoryAndClearCart() {
        // Update inventory
        for (CartItemVm item : cart.getItems()) {
            Product product = productService.findById(item.getProductId());
            int remainingQuantity = product.getQuantity() - item.getQuantity();
            product.setQuantity(Math.max(remainingQuantity, 0));
            productService.save(product);
        }

        // Clear cart
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setLastModifiedDate(new Date());
        applicationState.setCart(cart);
    }

    private void processPayment() {
        try {
            btnBuyNow.setText("Processing Payment...");
            btnBuyNow.setEnabled(false);
            isProcessingPayment = true;

            boolean success = paymentService.processPayment(order);

            if (success) {
                if (order.getPaymentMethod() == PaymentMethod.ZALOPAY
//                        ||
//                        order.getPaymentMethod() == PaymentMethod.MOMO
                ) {

                    currentAppTransId = order.getPaymentTransactionId();
                    // Display QR code using the service
                    boolean displayed = qrDisplayService.displayQRCodeForOrder(
                            order,
                            this,
                            (v) -> cancelPayment()
                    );

                    if (displayed) {
                        startStatusChecking();
                    } else {
                        cancelPayment();
                    }
                } else {
                    // For other payment methods (like cash)
                    completeTransaction();
                }
            } else {
                cancelPayment();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            cancelPayment();
        }
    }


    private void cancelPayment() {
        if (statusCheckTimer != null && statusCheckTimer.isRunning()) {
            statusCheckTimer.stop();
        }

        qrDisplayService.closeQRCodeDialog();
        btnBuyNow.setEnabled(true);
        btnBuyNow.setText("Buy Now");
        isProcessingPayment = false;
        currentAppTransId = null;

        if (order != null && order.getId() != null) {
            order = orderService.findById(order.getId());
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            order.setPaymentTransactionId(null);
            orderService.save(order);
        }
    }


    private Order createOrderFromCart() {
        Order newOrder = new Order();
        newOrder.setCreateDate(new Date());
        newOrder.setTotalAmount(cart.getTotalAmount());
        newOrder.setStatus(OrderStatus.IN_PROGRESS);
        newOrder.setActive(true);

        User user = new User();
        user.setUserId(currentUser.getUserId());
        newOrder.setUser(user);

        orderDetails = cart.getItems().stream()
                .map(CartItemVm::toOrderDetail)
                .peek(od -> od.setOrder(newOrder))
                .toList();
        newOrder.setOrderDetails(orderDetails);
        return newOrder;
    }

    private void startStatusChecking() {
        if (statusCheckTimer != null && statusCheckTimer.isRunning()) {
            statusCheckTimer.stop();
        }
        statusCheckTimer = new Timer(5000, (ActionEvent e) -> checkOrderStatus());
        statusCheckTimer.start();
    }

    private void checkOrderStatus() {
        if (currentAppTransId == null) {
            return;
        }
        try {
            PaymentStatus status = paymentService.checkPaymentStatus(order);

            if (status == PaymentStatus.COMPLETED) {
                statusCheckTimer.stop();
                qrDisplayService.closeQRCodeDialog();
                completeTransaction();
            } else if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED) {
                statusCheckTimer.stop();
                cancelPayment();
                btnBuyNow.setText("Try Again");
                btnBuyNow.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void completeTransaction() {
        order = orderService.findById(order.getId());

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setCreateDate(new Date());
        invoice.setCustomer(order.getCustomer()); // Use the customer from the order that was already saved
        invoice.setTotalAmount(cart.getTotalAmount());
        invoice.setPaymentMethod(order.getPaymentMethod());
        invoice.setPaymentStatus(order.getPaymentStatus());
        invoice.setPaymentTransactionId(order.getPaymentTransactionId());
        invoice.setShippingAddress(txtAddress.getText());
        invoice.setPhoneNumber(txtPhoneNumber.getText());

        try {
            invoice = invoiceService.save(invoice);

            List<InvoiceDetail> invoiceDetails = mapCartItemsToInvoiceDetails(cart.getItems(), invoice);
            invoice.setInvoiceDetail(invoiceDetails);
            // Save the invoice again with the details
            invoice= invoiceService.save(invoice);

            // Automatically export the invoice to PDF
            File pdfFile = null;
            try {
                pdfFile = invoiceExportService.exportInvoiceToPdfInInvoicesDir(invoice);

                // Open the PDF file with the default PDF viewer
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                    Toast.show(this, Toast.Type.SUCCESS,
                            "Hóa đơn đã được xuất tự động và lưu tại: " + pdfFile.getAbsolutePath(),
                            ToastLocation.TOP_CENTER);
                }
            } catch (Exception ex) {
                // Log the error but continue with the transaction
                ex.printStackTrace();
                Toast.show(this, Toast.Type.WARNING,
                        "Không thể xuất hóa đơn tự động. Bạn có thể xem hóa đơn sau bằng nút 'Xem hóa đơn'.",
                        ToastLocation.TOP_CENTER);
            }

            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.setStatus(OrderStatus.IN_PROGRESS);
            orderService.save(order);

            updateInventoryAndClearCart();

            // Navigate to completion screen
            RouteParams params = new RouteParams();
            if (pdfFile != null) {
                params.set("invoiceFilePath", pdfFile.getAbsolutePath());
            }
            params.set("invoiceId", invoice.getId().toString());
            params.set("customerName", invoice.getCustomer().getName());
            params.set("totalAmount", invoice.getTotalAmount().toString());
            navigationService.navigateTo(TransactionCompletedPanel.class, params);
        } catch (Exception e) {
            handleTransactionError(e);
        }
    }

    private void handleTransactionError(Exception e) {
        StringBuilder errorMessage = new StringBuilder(e.getMessage());
        if (errorMessage.length() > 150) {
            errorMessage = new StringBuilder(errorMessage.substring(0, 150) + "...");
        }

        Toast.show(this, Toast.Type.ERROR,
                "Error: " + errorMessage,
                ToastLocation.TOP_CENTER);

        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setPaymentTransactionId(null);
        orderService.save(order);
    }


    private boolean validateForm() {
        if (txtCustomerName.getText().trim().isEmpty()) {
            Toast.show(this, Toast.Type.ERROR,
                    "Customer name is required",
                    ToastLocation.TOP_CENTER);
            txtCustomerName.requestFocus();
            return false;
        }

        if (txtPhoneNumber.getText().trim().isEmpty()) {
            Toast.show(this, Toast.Type.ERROR,
                    "Phone number is required",
                    ToastLocation.TOP_CENTER);
            txtPhoneNumber.requestFocus();
            return false;
        }

        if (txtAddress.getText().trim().isEmpty()) {
            Toast.show(this, Toast.Type.ERROR,
                    "Address is required",
                    ToastLocation.TOP_CENTER);
            txtAddress.requestFocus();
            return false;
        }

        if (cart.getItems().isEmpty()) {
            Toast.show(this, Toast.Type.ERROR,
                    "Cart is empty. Please add items to cart before checkout.",
                    ToastLocation.TOP_CENTER);
            return false;
        }

        return true;
    }

    private void createOrder() {
        Customer customer = getCustomer();
        boolean isExistingCustomer = customer.getId() != null;
        customer = customerService.save(customer);


        if (isExistingCustomer) {
            Toast.show(this, Toast.Type.INFO,
                    "Sử dụng thông tin khách hàng đã tồn tại: " + customer.getName(),
                    ToastLocation.TOP_CENTER);
        }

        order.setCustomer(customer);
        order.setPaymentMethod((PaymentMethod) cmbPaymentMethod.getSelectedItem());
        orderService.save(order);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtPhoneNumber = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbPaymentMethod = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOrder = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        lblTranportFee = new javax.swing.JLabel();
        lblTotalPrice = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnBuyNow = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setText("Customer Name");

        txtCustomerName.setText("Đặng Hữu Hoài Nam");
        txtCustomerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerNameActionPerformed(evt);
            }
        });

        jLabel5.setText("Email");

        txtEmail.setText("24410068@ms.uit.edu.vn");
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        txtPhoneNumber.setText("0987654321");
        txtPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneNumberActionPerformed(evt);
            }
        });

        jLabel6.setText("Phone Number");

        txtAddress.setText("Khu phố 6, P.Linh Trung, Tp.Thủ Đức, Tp.Hồ Chí Minh");
        txtAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressActionPerformed(evt);
            }
        });

        jLabel7.setText("Address");

        jLabel1.setText("Phương thức thanh toán");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 60)); // NOI18N
        jLabel3.setText("←");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        txtOrder.setColumns(20);
        txtOrder.setRows(5);
        jScrollPane1.setViewportView(txtOrder);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTranportFee.setText("Tranport fee:");

        lblTotalPrice.setText("Total sale price:");

        lblTotal.setText("Total:");

        btnBuyNow.setBackground(new java.awt.Color(246, 127, 26));
        btnBuyNow.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBuyNow.setForeground(new java.awt.Color(255, 255, 255));
        btnBuyNow.setText("Buy Now");
        btnBuyNow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuyNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuyNowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(674, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTranportFee)
                    .addComponent(lblTotalPrice)
                    .addComponent(lblTotal)
                    .addComponent(btnBuyNow, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(lblTotalPrice)
                .addGap(18, 18, 18)
                .addComponent(lblTranportFee)
                .addGap(26, 26, 26)
                .addComponent(lblTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuyNow)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private void btnBuyNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuyNowActionPerformed
        if (!validateForm()) {
            return;
        }
        createOrder();
        processPayment();

    }//GEN-LAST:event_btnBuyNowActionPerformed

    private void txtCustomerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerNameActionPerformed
        loadInvoice(); // Update invoice display when customer name changes
    }//GEN-LAST:event_txtCustomerNameActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        loadInvoice(); // Update invoice display when email changes
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneNumberActionPerformed
        loadInvoice(); // Update invoice display when phone number changes
    }//GEN-LAST:event_txtPhoneNumberActionPerformed

    private void txtAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressActionPerformed
        loadInvoice(); // Update invoice display when address changes
    }//GEN-LAST:event_txtAddressActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        navigationService.navigateTo(AppView.CART);
    }//GEN-LAST:event_jLabel3MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuyNow;
    private javax.swing.JComboBox<PaymentMethod> cmbPaymentMethod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalPrice;
    private javax.swing.JLabel lblTranportFee;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextArea txtOrder;
    private javax.swing.JTextField txtPhoneNumber;
    // End of variables declaration//GEN-END:variables
}

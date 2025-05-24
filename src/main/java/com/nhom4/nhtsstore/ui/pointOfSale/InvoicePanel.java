/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.nhom4.nhtsstore.ui.pointOfSale;

import com.nhom4.nhtsstore.entities.*;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.enums.DeliveryStatus;
import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.enums.FulfilmentMethod;
import com.nhom4.nhtsstore.services.*;
import com.nhom4.nhtsstore.services.impl.QRDisplayService;
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
import javax.swing.text.*;

@Scope("prototype")
@Component
@Slf4j
public class InvoicePanel extends javax.swing.JPanel implements RoutablePanel {

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
    private UserSessionVm currentUser;
    private List<OrderDetail> orderDetails;
    private Timer statusCheckTimer;
    private Timer transactionTimeoutTimer;
    private String currentAppTransId;
    private PaymentMethod selectedPaymentMethod = PaymentMethod.CASH;
    private boolean isProcessingPayment = false;
    private final int timeOutTransaction = 60 * 10000; // 10 minutes

    public InvoicePanel(ApplicationState applicationState, IOrderService orderService,
                        IPaymentService paymentService, IInvoiceService invoiceService,
                        ICustomerService customerService, IProductService productService,
                        NavigationService navigationService, QRDisplayService qrDisplayService,
                        IInvoiceExportService invoiceExportService) {
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
        setupUI();
    }

    private void setupUI() {
        setupComboBox();
        setupDocumentListeners();
        configureTextFields();
    }

    private void setupDocumentListeners() {
        javax.swing.event.DocumentListener documentListener = new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { loadInvoice(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { loadInvoice(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { loadInvoice(); }
        };

        JTextField[] fields = {txtCustomerName, txtEmail, txtPhoneNumber};
        for (JTextField field : fields) {
            field.getDocument().addDocumentListener(documentListener);
        }
        txtAddress.getDocument().addDocumentListener(documentListener);
    }

    @Override
    public void onNavigate(RouteParams params) {
        currentUser = applicationState.getCurrentUser();
        cart = applicationState.getCart();
        loadInvoice();
        customerFormPanel.setVisible(true);

        FulfilmentMethod initialMethod = (FulfilmentMethod) cmbShippingMethod.getSelectedItem();
        boolean isPickup = initialMethod == FulfilmentMethod.CUSTOMER_TAKEAWAY;
        setAddressFieldsVisible(!isPickup);
    }

    private void setAddressFieldsVisible(boolean visible) {
        txtAddress.setVisible(visible);
        scrollAddress.setVisible(visible);
        jLabel7.setVisible(visible); // Address label
    }

    private void setupComboBox() {
        // Payment method setup
        cmbPaymentMethod.setModel(new DefaultComboBoxModel<>(PaymentMethod.values()));
        cmbPaymentMethod.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                PaymentMethod newMethod = (PaymentMethod) e.getItem();
                if (newMethod != selectedPaymentMethod && isProcessingPayment) {
                    Toast.show(this, Toast.Type.WARNING,
                            "Cannot change payment method during payment processing",
                            ToastLocation.TOP_CENTER);
                    cmbPaymentMethod.setSelectedItem(selectedPaymentMethod);
                    return;
                }
                selectedPaymentMethod = newMethod;
            }
        });

        // Shipping method setup
        cmbShippingMethod.setModel(new DefaultComboBoxModel<>(FulfilmentMethod.values()));
        cmbShippingMethod.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean isPickup = isPickUp();
                setAddressFieldsVisible(!isPickup);

                BigDecimal transportFee = isPickup ? BigDecimal.ZERO : BigDecimal.valueOf(5.00);
                lblTranportFee.setText("Transport fee: " + transportFee + " $");
                lblTranportFee.setVisible(!isPickup);

                loadInvoice();
            }
        });
    }

    private void configureTextFields() {
        // Configure regular text fields
        JTextField[] fields = {txtCustomerName, txtEmail, txtPhoneNumber};
        int preferredWidth = 200;
        int maxChars = 50;

        for (JTextField field : fields) {
            field.setPreferredSize(new Dimension(preferredWidth, field.getPreferredSize().height));
            field.setMaximumSize(field.getPreferredSize());
            addLengthLimit((AbstractDocument) field.getDocument(), maxChars);
        }

        // Configure address text area
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        addLengthLimit((AbstractDocument) txtAddress.getDocument(), 200);

        // Configure order display
        txtOrder.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtOrder.setEditable(false);
    }

    private void addLengthLimit(AbstractDocument doc, int maxLength) {
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length()) <= maxLength) {
                    super.insertString(fb, offset, text, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - maxLength - length;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (!text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void loadInvoice() {
        StringBuilder sb = new StringBuilder();
        sb.append("*************************************************************Invoice Detail**************************************************************\n");
        sb.append("****************************************************************************************************************************************\n");

        String customerName = txtCustomerName.getText().trim();
        String email = txtEmail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String address = txtAddress.getText().trim();

        // Add customer info if provided
        if (!customerName.isEmpty() || !email.isEmpty() || !phoneNumber.isEmpty() || !address.isEmpty()) {
            sb.append("Customer Information:\n");
            appendIfNotEmpty(sb, "Name: ", customerName);
            appendIfNotEmpty(sb, "Email: ", email);
            appendIfNotEmpty(sb, "Phone: ", phoneNumber);
            appendIfNotEmpty(sb, "Address: ", address);
            sb.append("--------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        }

        // Add items
        sb.append("Item Name:\t\t\t\t").append("Number \t\t\t").append("Price($)\n");
        cart.getItems().forEach(item -> {
            sb.append(item.getProductName())
                    .append("\t\t\t")
                    .append(item.getQuantity())
                    .append("\t\t\t")
                    .append(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .append("\n");
        });

        txtOrder.setText(sb.toString());

        // Update price labels
        BigDecimal totalPrice = cart.getTotalAmount();
        lblTotalPrice.setText("Total sale price: " + totalPrice + " $");

        boolean isPickup = isPickUp();
        BigDecimal transportFee = isPickup ? BigDecimal.ZERO : BigDecimal.valueOf(5.00);
        lblTranportFee.setText("Transport fee: " + transportFee + " $");

        BigDecimal finalTotal = totalPrice.add(transportFee);
        lblTotal.setText("Total: " + finalTotal + " $");
    }

    private void appendIfNotEmpty(StringBuilder sb, String label, String value) {
        if (!value.isEmpty()) {
            sb.append(label).append(value).append("\n");
        }
    }

    private List<InvoiceDetail> mapCartItemsToInvoiceDetails(List<CartItemVm> items, Invoice invoice) {
        return items.stream().map(item -> {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setProduct(Product.builder().id(item.getProductId()).build());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());
            detail.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return detail;
        }).toList();
    }

    private Customer getCustomer() {
        String name = txtCustomerName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNumber.getText().trim();
        String address = txtAddress.getText().trim();

        if (!email.isEmpty() || !phone.isEmpty()) {
            Customer existingCustomer = customerService.findByEmailOrPhoneNumber(email, phone);
            if (existingCustomer != null) {
                existingCustomer.setName(name);
                if (!email.isEmpty()) existingCustomer.setEmail(email);
                if (!phone.isEmpty()) existingCustomer.setPhoneNumber(phone);
                existingCustomer.setAddress(address);
                return existingCustomer;
            }
        }
        //new Customer(null, name, email, phone, address)
        return Customer.builder()
                .name(name)
                .email(email)
                .phoneNumber(phone)
                .address(address)
                .build();
    }

    private void updateInventoryAndClearCart() {
        // Update inventory
        for (CartItemVm item : cart.getItems()) {
            Product product = productService.findById(item.getProductId());
            product.setQuantity(Math.max(product.getQuantity() - item.getQuantity(), 0));
            productService.save(product);
        }

        // Clear cart
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setLastModifiedDate(new Date());
        applicationState.setCart(cart);
    }

    private void processPayment(Long orderId) {
        try {
            btnBuyNow.setText("Processing Payment...");
            btnBuyNow.setEnabled(false);
            isProcessingPayment = true;

            Order order = orderService.findById(orderId);
            startTransactionTimeout(orderId);

            // Handle cash payment
            if (order.getPaymentMethod() == PaymentMethod.CASH) {
                order.setPaymentStatus(PaymentStatus.COMPLETED);
                order.setPaymentTransactionId("CASH_" + order.getId());
                orderService.save(order);
                stopTransactionTimeout();
                completeTransaction(orderId);
                return;
            }

            // Handle electronic payment
            boolean success = paymentService.processPayment(order);
            currentAppTransId = order.getPaymentTransactionId();
            log.info("Processing payment for order: {} with transaction id: {}", orderId, currentAppTransId);
            order.setPaymentTransactionId(currentAppTransId);
            orderService.save(order);

            if (success) {
                // For electronic payments, show QR code
                if (order.getPaymentMethod() != PaymentMethod.CASH) {
                    boolean qrDisplayed = qrDisplayService.displayQRCodeForOrder(
                            order,
                            this,
                            unused -> cancelPayment(orderId)
                    );

                    if (qrDisplayed) {
                        startStatusChecking(orderId);
                    } else {
                        log.error("Failed to display QR code for payment");
                        cancelPayment(orderId);
                        Toast.show(this, Toast.Type.ERROR,
                                "Failed to generate payment QR code. Please try again.",
                                ToastLocation.TOP_CENTER);
                    }
                } else {
                    // For cash payments, complete immediately

                    completeTransaction(orderId);
                }
            }
        } catch (Exception ex) {
            log.error("Error processing payment", ex);
            cancelPayment(orderId);
        }
    }

    private void cancelPayment(Long orderId) {
        if (statusCheckTimer != null && statusCheckTimer.isRunning()) {
            statusCheckTimer.stop();
        }

        stopTransactionTimeout();
        qrDisplayService.closeQRCodeDialog();
        btnBuyNow.setEnabled(true);
        btnBuyNow.setText("Buy Now");
        isProcessingPayment = false;

        if (orderId != null) {
            Order order = orderService.findById(orderId);
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            order.setPaymentTransactionId(currentAppTransId);
            order.setDeliveryStatus(DeliveryStatus.CANCELLED);
            orderService.save(order);
        }
    }

    private Order createOrderFromCart() {
        Order newOrder = new Order();
        newOrder.setCreateDate(new Date());
        newOrder.setTotalAmount(cart.getTotalAmount());
        newOrder.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
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

    private void startTransactionTimeout(Long orderId) {
        stopTransactionTimeout();

        transactionTimeoutTimer = new Timer(timeOutTransaction, e -> {
            log.warn("Payment transaction timed out after {} minutes", timeOutTransaction / 60000);
            Toast.show(this, Toast.Type.WARNING,
                    "Transaction timed out. Please try again.",
                    ToastLocation.TOP_CENTER);
            cancelPayment(orderId);
        });
        transactionTimeoutTimer.setRepeats(false);
        transactionTimeoutTimer.start();
        log.debug("Transaction timeout timer started: {} minutes", timeOutTransaction / 60000);
    }

    private void stopTransactionTimeout() {
        if (transactionTimeoutTimer != null && transactionTimeoutTimer.isRunning()) {
            transactionTimeoutTimer.stop();
            log.debug("Transaction timeout timer stopped");
        }
    }

    private void startStatusChecking(Long orderId) {
        if (statusCheckTimer != null && statusCheckTimer.isRunning()) {
            statusCheckTimer.stop();
        }
        statusCheckTimer = new Timer(4000, e -> checkOrderStatus(orderId));
        statusCheckTimer.start();
    }

    private void checkOrderStatus(Long orderId) {
        if (currentAppTransId == null) return;

        try {
            Order order = orderService.findById(orderId);
            PaymentStatus status = paymentService.checkPaymentStatus(order);

            if (status == PaymentStatus.COMPLETED) {
                if (statusCheckTimer != null) {
                    statusCheckTimer.stop();
                }
                completeTransaction(orderId);
            } else if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED) {
                cancelPayment(orderId);
                Toast.show(this, Toast.Type.ERROR,
                        "Payment failed or was cancelled. Please try again.",
                        ToastLocation.TOP_CENTER);
            }
        } catch (Exception ex) {
            log.error("Error checking payment status", ex);
        }
    }

    private void completeTransaction(Long orderId) {
        stopTransactionTimeout();
        qrDisplayService.closeQRCodeDialog();

        Order order = orderService.findById(orderId);

        try {
            // Create invoice
           Invoice invoice= createInvoiceFromOrder(order);

            // Export invoice to PDF if possible
            File pdfFile = null;
            try {
                pdfFile = invoiceExportService.exportInvoiceToPdfInInvoicesDir(invoice);            } catch (Exception ex) {
                log.error("Error exporting invoice to PDF", ex);
            }

            // Update order status
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            orderService.save(order);

            // Update inventory and clear cart
            updateInventoryAndClearCart();

            // Navigate to completion screen
            navigateToCompletionScreen(invoice, order.getCustomer(), pdfFile);

        } catch (Exception e) {
            handleTransactionError(e, orderId);
        }
    }

    private Invoice createInvoiceFromOrder(Order order) {
        Invoice invoice = new Invoice();
        FulfilmentMethod fulfilmentMethod = order.getFulfilmentMethod();
        boolean isPickup = fulfilmentMethod == FulfilmentMethod.CUSTOMER_TAKEAWAY;

        invoice.setFulfilmentMethod(fulfilmentMethod);
        invoice.setCustomer(order.getCustomer());
        invoice.setPhoneNumber(order.getCustomer() != null ? order.getCustomer().getPhoneNumber() : null);

        if (isPickup) {
            invoice.setDeliveryAddress(null);
            invoice.setDeliveryFee(null);
        } else {
            invoice.setDeliveryAddress(order.getDeliveryAddress());
            invoice.setDeliveryFee(BigDecimal.valueOf(5.00));
        }

        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setCreateDate(new Date());
        invoice.setPaymentMethod(order.getPaymentMethod());
        invoice.setPaymentStatus(PaymentStatus.COMPLETED);
        invoice.setPaymentTransactionId(order.getPaymentTransactionId());
        List<InvoiceDetail> invoiceDetails = mapCartItemsToInvoiceDetails(cart.getItems(), invoice);
        invoice.setInvoiceDetail(invoiceDetails);

        return invoiceService.save(invoice);
    }

    private void navigateToCompletionScreen(Invoice invoice, Customer customer, File pdfFile) {
        RouteParams params = new RouteParams();

        if (pdfFile != null) {
            params.set("invoiceFilePath", pdfFile.getAbsolutePath());
        }

        params.set("invoiceId", invoice.getId().toString());

        if (customer != null) {
            params.set("customerName", customer.getName());
        } else {
            params.set("customerName", "Guest");
        }

        params.set("totalAmount", invoice.getTotalAmount().toString());
        navigationService.navigateTo(TransactionCompletedPanel.class, params);
    }

    private void handleTransactionError(Exception e, Long orderId) {
        String errorMessage = e.getMessage();
        if (errorMessage != null && errorMessage.length() > 150) {
            errorMessage = errorMessage.substring(0, 150) + "...";
        }

        Toast.show(this, Toast.Type.ERROR,
                "Error: " + errorMessage,
                ToastLocation.TOP_CENTER);

        Order order = orderService.findById(orderId);
        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setPaymentTransactionId(null);
        orderService.save(order);
    }

    private boolean validateForm() {
        boolean isPickup = isPickUp();

        if (!isPickup) {
            // Delivery validation
            if (isFieldEmpty(txtCustomerName, "Customer name is required") ||
                    isFieldEmpty(txtPhoneNumber, "Phone number is required") ||
                    isFieldEmpty(txtAddress, "Address is required")) {
                return false;
            }
        } else {
            // Pickup validation - require at least contact method for customer identification
            if (txtEmail.getText().trim().isEmpty() && txtPhoneNumber.getText().trim().isEmpty()) {
                showError("Either email or phone number is required for customer identification");
                txtPhoneNumber.requestFocus();
                return false;
            }
        }

        if (cart.getItems().isEmpty()) {
            showError("Cart is empty. Please add items to cart before checkout.");
            return false;
        }

        return true;
    }

    private boolean isFieldEmpty(JTextComponent field, String errorMessage) {
        if (field.getText().trim().isEmpty()) {
            showError(errorMessage);
            field.requestFocus();
            return true;
        }
        return false;
    }

    private void showError(String message) {
        Toast.show(this, Toast.Type.ERROR, message, ToastLocation.TOP_CENTER);
    }

    private Long createOrder(Order newOrder) {
        FulfilmentMethod selectedMethod = (FulfilmentMethod) cmbShippingMethod.getSelectedItem();
        boolean isPickup = selectedMethod == FulfilmentMethod.CUSTOMER_TAKEAWAY;

        // Handle customer data
        String email = txtEmail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String customerName = txtCustomerName.getText().trim();

        boolean shouldCreateCustomer = !isPickup ||
                ((!email.isEmpty() || !phoneNumber.isEmpty()) && !customerName.isEmpty());

        if (shouldCreateCustomer) {
            Customer customer = getCustomer();
            boolean isExistingCustomer = customer.getId() != null;
            customer = customerService.save(customer);

            if (isExistingCustomer) {
                Toast.show(this, Toast.Type.INFO,
                        "Using existing customer: " + customer.getName(),
                        ToastLocation.TOP_CENTER);
            }

            newOrder.setCustomer(customer);
        } else {
            newOrder.setCustomer(null);
        }

        // Configure order based on fulfillment method
        if (isPickup) {
            newOrder.setDeliveryAddress(null);
            newOrder.setDeliveryStatus(DeliveryStatus.COMPLETED);
            newOrder.setDeliveryFee(null);
            newOrder.setTotalAmount(cart.getTotalAmount());
        } else {
            newOrder.setDeliveryAddress(txtAddress.getText().trim());
            newOrder.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
            newOrder.setDeliveryFee(BigDecimal.valueOf(5.00));
            newOrder.setTotalAmount(cart.getTotalAmount());
        }

        // Set payment details
        newOrder.setPaymentMethod((PaymentMethod) cmbPaymentMethod.getSelectedItem());
        newOrder.setPaymentStatus(newOrder.getPaymentMethod() == PaymentMethod.CASH ?
                PaymentStatus.COMPLETED : PaymentStatus.PENDING);
        newOrder.setFulfilmentMethod(selectedMethod);

        return orderService.save(newOrder).getId();
    }

    private boolean isPickUp() {
        return cmbShippingMethod.getSelectedItem() == FulfilmentMethod.CUSTOMER_TAKEAWAY;
    }


    private void btnBuyNowActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateForm()) {
            return;
        }

        // Create the order from cart and save it
        Order newOrder = createOrderFromCart();
        Long orderId = createOrder(newOrder);

        // Process payment with the order ID
        processPayment(orderId);
    }//GEN-LAST:event_btnBuyNowActionPerformed

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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        customerFormPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JTextField();
        txtCustomerName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        scrollAddress = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOrder = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        lblTranportFee = new javax.swing.JLabel();
        lblTotalPrice = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnBuyNow = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cmbPaymentMethod = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cmbShippingMethod = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 60)); // NOI18N
        jLabel3.setText("←");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        customerFormPanel.setBackground(java.awt.SystemColor.window);
        customerFormPanel.setEnabled(false);
        customerFormPanel.setMaximumSize(new java.awt.Dimension(20, 20));
        customerFormPanel.setOpaque(false);

        jLabel6.setText("Phone Number");

        txtPhoneNumber.setMaximumSize(new java.awt.Dimension(0, 0));
        txtPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneNumberActionPerformed(evt);
            }
        });

        txtCustomerName.setMaximumSize(new java.awt.Dimension(0, 0));
        txtCustomerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerNameActionPerformed(evt);
            }
        });

        jLabel5.setText("Email");

        txtEmail.setMaximumSize(new java.awt.Dimension(0, 0));
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        jLabel7.setText("Address");

        jLabel4.setText("Customer Name");

        scrollAddress.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtAddress.setColumns(20);
        txtAddress.setLineWrap(true);
        txtAddress.setRows(5);
        txtAddress.setWrapStyleWord(true);
        scrollAddress.setViewportView(txtAddress);

        javax.swing.GroupLayout customerFormPanelLayout = new javax.swing.GroupLayout(customerFormPanel);
        customerFormPanel.setLayout(customerFormPanelLayout);
        customerFormPanelLayout.setHorizontalGroup(
            customerFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customerFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCustomerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(customerFormPanelLayout.createSequentialGroup()
                        .addGroup(customerFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(scrollAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                .addContainerGap())
        );
        customerFormPanelLayout.setVerticalGroup(
            customerFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customerFormPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(customerFormPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLabel2)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel2, java.awt.BorderLayout.LINE_START);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtOrder.setColumns(20);
        txtOrder.setRows(5);
        jScrollPane1.setViewportView(txtOrder);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTranportFee.setText("Tranport fee:");

        lblTotalPrice.setText("Total sale price:");

        lblTotal.setText("Total:");

        btnBuyNow.setBackground(new java.awt.Color(246, 127, 26));
        btnBuyNow.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBuyNow.setForeground(new java.awt.Color(255, 255, 255));
        btnBuyNow.setText("Payment");
        btnBuyNow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuyNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuyNowActionPerformed(evt);
            }
        });

        jLabel1.setText("Phương thức thanh toán");

        jLabel8.setText("Phương thức giao hàng");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(cmbShippingMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 400, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTranportFee)
                            .addComponent(lblTotalPrice)
                            .addComponent(lblTotal)
                            .addComponent(btnBuyNow, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(63, 63, 63))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalPrice)
                        .addGap(18, 18, 18)
                        .addComponent(lblTranportFee))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(4, 4, 4)
                        .addComponent(cmbShippingMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(lblTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuyNow)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents



    private void txtCustomerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerNameActionPerformed
        loadInvoice(); // Update invoice display when customer name changes
    }//GEN-LAST:event_txtCustomerNameActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        loadInvoice(); // Update invoice display when email changes
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneNumberActionPerformed
        loadInvoice(); // Update invoice display when phone number changes
    }//GEN-LAST:event_txtPhoneNumberActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        navigationService.navigateTo(AppView.CART);
    }//GEN-LAST:event_jLabel3MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuyNow;
    private javax.swing.JComboBox<PaymentMethod> cmbPaymentMethod;
    private javax.swing.JComboBox<FulfilmentMethod> cmbShippingMethod;
    private javax.swing.JPanel customerFormPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalPrice;
    private javax.swing.JLabel lblTranportFee;
    private javax.swing.JScrollPane scrollAddress;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextArea txtOrder;
    private javax.swing.JTextField txtPhoneNumber;
    // End of variables declaration//GEN-END:variables
}

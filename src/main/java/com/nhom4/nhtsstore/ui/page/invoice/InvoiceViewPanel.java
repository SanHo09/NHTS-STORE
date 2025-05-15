package com.nhom4.nhtsstore.ui.page.invoice;

import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.entities.InvoiceDetail;
import com.nhom4.nhtsstore.services.IInvoiceExportService;
import com.nhom4.nhtsstore.services.IInvoiceService;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;


@Component
public class InvoiceViewPanel extends JPanel implements RoutablePanel {

    @Autowired
    private NavigationService navigationService;

    @Autowired
    private IInvoiceService invoiceService;

    @Autowired
    private IInvoiceExportService invoiceExportService;

    private Invoice invoice;
    private File invoiceFile;

    private JTextField txtInvoiceId;
    private JTextField txtCreateDate;
    private JTextField txtCustomerName;
    private JTextField txtCustomerEmail;
    private JTextField txtCustomerPhone;
    private JTextField txtShippingAddress;
    private JTextField txtPaymentMethod;
    private JTextField txtPaymentStatus;
    private JTextField txtPaymentTransactionId;
    private JTextField txtTotalAmount;
    private JTable tblInvoiceDetails;
    private DefaultTableModel tableModel;

    public InvoiceViewPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Invoice Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back to List");
        backButton.addActionListener(e -> navigationService.navigateBack());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel invoiceInfoPanel = createInvoiceInfoPanel();
        contentPanel.add(invoiceInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel customerInfoPanel = createCustomerInfoPanel();
        contentPanel.add(customerInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel paymentInfoPanel = createPaymentInfoPanel();
        contentPanel.add(paymentInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel invoiceDetailsPanel = createInvoiceDetailsPanel();
        contentPanel.add(invoiceDetailsPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewInvoiceButton = new JButton("View Invoice PDF");
        viewInvoiceButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewInvoiceButton.setBackground(new Color(0, 123, 255));
        viewInvoiceButton.setForeground(Color.WHITE);
        viewInvoiceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewInvoiceButton.addActionListener(e -> openInvoice());
        footerPanel.add(viewInvoiceButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createInvoiceInfoPanel() {
        JPanel panel = createSectionPanel("Invoice Information");

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        formPanel.add(createLabelPanel("Invoice ID:"));
        txtInvoiceId = createReadOnlyTextField();
        formPanel.add(txtInvoiceId);

        formPanel.add(createLabelPanel("Create Date:"));
        txtCreateDate = createReadOnlyTextField();
        formPanel.add(txtCreateDate);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomerInfoPanel() {
        JPanel panel = createSectionPanel("Customer Information");

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(createLabelPanel("Customer Name:"));
        txtCustomerName = createReadOnlyTextField();
        formPanel.add(txtCustomerName);

        formPanel.add(createLabelPanel("Email:"));
        txtCustomerEmail = createReadOnlyTextField();
        formPanel.add(txtCustomerEmail);

        formPanel.add(createLabelPanel("Phone Number:"));
        txtCustomerPhone = createReadOnlyTextField();
        formPanel.add(txtCustomerPhone);

        formPanel.add(createLabelPanel("Shipping Address:"));
        txtShippingAddress = createReadOnlyTextField();
        formPanel.add(txtShippingAddress);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaymentInfoPanel() {
        JPanel panel = createSectionPanel("Payment Information");

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(createLabelPanel("Payment Method:"));
        txtPaymentMethod = createReadOnlyTextField();
        formPanel.add(txtPaymentMethod);

        formPanel.add(createLabelPanel("Payment Status:"));
        txtPaymentStatus = createReadOnlyTextField();
        formPanel.add(txtPaymentStatus);

        formPanel.add(createLabelPanel("Transaction ID:"));
        txtPaymentTransactionId = createReadOnlyTextField();
        formPanel.add(txtPaymentTransactionId);

        formPanel.add(createLabelPanel("Total Amount:"));
        txtTotalAmount = createReadOnlyTextField();
        formPanel.add(txtTotalAmount);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInvoiceDetailsPanel() {
        JPanel panel = createSectionPanel("Invoice Details");

        String[] columnNames = {"Product", "Quantity", "Unit Price (USD)", "Subtotal (USD)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells read-only
            }
        };
        tblInvoiceDetails = new JTable(tableModel);
        tblInvoiceDetails.setFillsViewportHeight(true);
        tblInvoiceDetails.setRowHeight(25);

        // Center align the quantity column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblInvoiceDetails.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Right align the price columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblInvoiceDetails.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblInvoiceDetails.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane tableScrollPane = new JScrollPane(tblInvoiceDetails);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    private JPanel createLabelPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label);
        return panel;
    }

    private JTextField createReadOnlyTextField() {
        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setBackground(new Color(245, 245, 245));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private void loadInvoiceData() {
        if (invoice == null) {
            return;
        }

        // Format date using lastModifiedOn
        String formattedDate = invoice.getLastModifiedOn() != null ?
                invoice.getLastModifiedOn().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
        // Set invoice information
        txtInvoiceId.setText(invoice.getId().toString());
        txtCreateDate.setText(formattedDate);

        // Set customer information
        if (invoice.getCustomer() != null) {
            txtCustomerName.setText(invoice.getCustomer().getName());
            txtCustomerEmail.setText(invoice.getCustomer().getEmail());
            txtCustomerPhone.setText(invoice.getPhoneNumber());
        }
        txtShippingAddress.setText(invoice.getShippingAddress());

        // Set payment information
        txtPaymentMethod.setText(invoice.getPaymentMethod() != null ? 
                invoice.getPaymentMethod().getDisplayName() : "");
        txtPaymentStatus.setText(invoice.getPaymentStatus() != null ? 
                invoice.getPaymentStatus().getDisplayName() : "");
        txtPaymentTransactionId.setText(invoice.getPaymentTransactionId());
        txtTotalAmount.setText(invoice.getTotalAmount() + " USD");

        // Clear and populate invoice details table
        tableModel.setRowCount(0);
        if (invoice.getInvoiceDetail() != null) {
            for (InvoiceDetail detail : invoice.getInvoiceDetail()) {
                String productName = detail.getProduct() != null && detail.getProduct().getName() != null ? 
                        detail.getProduct().getName() : "Unknown Product";
                Object[] row = {
                    productName,
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getSubtotal()
                };
                tableModel.addRow(row);
            }
        }
        // Try to generate the invoice PDF file
        try {
            invoiceFile = invoiceExportService.exportInvoiceToPdf(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openInvoice() {
        try {
            if (invoiceFile != null && invoiceFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(invoiceFile);
            } else {
                // If the file doesn't exist, try to generate it again
                try {
                    invoiceFile = invoiceExportService.exportInvoiceToPdf(invoice);
                    if (invoiceFile.exists()) {
                        Desktop.getDesktop().open(invoiceFile);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                                "Could not generate invoice PDF file.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Error generating invoice PDF: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error opening invoice: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onNavigate(RouteParams params) {
        String invoiceId = params.get("entity", Invoice.class).getId().toString();
        invoice = invoiceService.findById(Long.parseLong(invoiceId));
        loadInvoiceData();
    }
}

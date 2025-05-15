/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.nhom4.nhtsstore.ui.pointOfSale;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.IconUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;

/**
 *
 * @author Sang
 */
@Component
public class TransactionCompletedPanel extends JPanel implements RoutablePanel {

    @Autowired
    private NavigationService navigationService;

    private String invoiceFilePath;
    private String invoiceId;
    private String customerName;
    private String totalAmount;

    private JPanel contentPanel;

    /**
     * Creates new form TransactionCompletedPanel
     */
    public TransactionCompletedPanel() {
        initComponents();
    }
    private void initComponents() {
        setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        contentPanel.setBackground(Color.WHITE);

        add(contentPanel, BorderLayout.CENTER);
    }
    @Override
    public void onNavigate(RouteParams params) {
        invoiceFilePath = params.get("invoiceFilePath", String.class);
        invoiceId = params.get("invoiceId", String.class);
        customerName = params.get("customerName", String.class);
        totalAmount = params.get("totalAmount", String.class);

        refreshPanelContent();
    }

    private void refreshPanelContent() {
        contentPanel.removeAll();

        JLabel iconSuccess = new JLabel();
        iconSuccess.setIcon(IconUtil.createSwingIconFromSvg(
                "/icons/EpSuccessFilled.svg",
                100, 100,
                color -> Color.GREEN
        ));
        iconSuccess.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        contentPanel.add(iconSuccess);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel titleLabel = new JLabel("Transaction Completed Successfully!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel invoiceInfoPanel = createInvoiceInfoPanel();
        invoiceInfoPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        contentPanel.add(invoiceInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        if (invoiceFilePath != null && !invoiceFilePath.isEmpty()) {
            JButton viewInvoiceButton = new JButton("View Invoice");
            viewInvoiceButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            viewInvoiceButton.setBackground(new Color(0, 123, 255));
            viewInvoiceButton.setForeground(Color.WHITE);
            viewInvoiceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            viewInvoiceButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            viewInvoiceButton.addActionListener(e -> openInvoice());
            contentPanel.add(viewInvoiceButton);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JButton backButton = new JButton("Back to Point of Sale");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            RouteParams params = new RouteParams();
            navigationService.navigateTo(AppView.POINT_OF_SALE, params);
        });
        contentPanel.add(backButton);

        // Refresh the panel
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createInvoiceInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 30, 20, 30)
        ));
        panel.setBackground(new Color(248, 249, 250));
        panel.setMaximumSize(new Dimension(500, 300));

        JLabel infoTitle = new JLabel("Invoice Information");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        panel.add(infoTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        if (invoiceId != null) {
            addInfoRow(panel, "Invoice Number:", invoiceId);
        }

        if (customerName != null) {
            addInfoRow(panel, "Customer Name:", customerName);
        }

        if (totalAmount != null) {
            try {
                BigDecimal amount = new BigDecimal(totalAmount);
                addInfoRow(panel, "Total Amount:", amount + " USD");
            } catch (NumberFormatException e) {
                addInfoRow(panel, "Total Amount:", totalAmount + " USD");
            }
        }

        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setPreferredSize(new Dimension(150, 25));
        labelComponent.setMinimumSize(new Dimension(150, 25));
        labelComponent.setMaximumSize(new Dimension(150, 25));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        rowPanel.add(labelComponent);
        rowPanel.add(valueComponent);

        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void openInvoice() {
        try {
            File pdfFile = new File(invoiceFilePath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Could not open invoice file: " + invoiceFilePath, 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error opening invoice: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }


}

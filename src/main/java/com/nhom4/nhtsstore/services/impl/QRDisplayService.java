package com.nhom4.nhtsstore.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.services.payment.PaymentStrategy;
import com.nhom4.nhtsstore.services.payment.PaymentStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Slf4j
public class QRDisplayService {
    private final PaymentStrategyFactory strategyFactory;
    private JDialog currentQrDialog; // Store reference to the current QR dialog

    @Autowired
    public QRDisplayService(PaymentStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    /**
     * Closes the currently displayed QR code dialog if it exists
     */
    public void closeQRCodeDialog() {
        if (currentQrDialog != null && currentQrDialog.isVisible()) {
            log.debug("Closing QR code dialog");
            currentQrDialog.dispose();
            currentQrDialog = null;
        }
    }

    /**
     * Displays a QR code dialog for the given order
     * @param order The order requiring payment
     * @param parent The parent component for dialog positioning
     * @param onCancel Callback when payment is canceled by user
     * @return true if QR was successfully displayed, false otherwise
     */
    public boolean displayQRCodeForOrder(Order order, Component parent, Consumer<Void> onCancel) {
        log.debug("displayQRCodeForOrder called with order: {}", (order != null ? order.getId() : "null"));

        if (order == null || order.getPaymentMethod() == null) {
            log.debug("Order or payment method is null, returning false");
            return false;
        }

        log.debug("Payment method: {}", order.getPaymentMethod());

        // Get the appropriate payment strategy
        PaymentStrategy strategy = strategyFactory.getStrategy(order.getPaymentMethod());
        log.debug("Got payment strategy: {}", strategy.getClass().getSimpleName());

        // Get QR code URL from the strategy
        log.debug("Getting QR code URL for transaction ID: {}", order.getPaymentTransactionId());
        Optional<String> qrUrl = strategy.getQRCodeUrl(order);
        log.debug("QR URL: {}", qrUrl.orElse("No QR URL available"));

        if (!qrUrl.isPresent()) {
            log.debug("QR URL is not present, returning false");
            return false;
        }

        try {
            log.debug("Generating QR code from URL: {}", qrUrl.get());
            // Generate QR code
            BufferedImage qrImage = generateQRCode(qrUrl.get());
            log.debug("QR code generated successfully");

            // Create and display dialog
            log.debug("Showing QR code dialog");
            showQRCodeDialog(qrImage, parent, strategy, order, onCancel);
            log.debug("QR code dialog shown successfully");
            return true;
        } catch (Exception e) {
            log.error("Error displaying QR code: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private BufferedImage generateQRCode(String url) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                url,
                BarcodeFormat.QR_CODE,
                300, // width
                300  // height
        );
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private void showQRCodeDialog(BufferedImage qrImage, Component parent,
                                  PaymentStrategy strategy, Order order,
                                  Consumer<Void> onCancel) {
        log.debug("Creating QR code dialog");
        // Get the parent frame
        Frame parentFrame = null;
        if (parent instanceof Frame) {
            parentFrame = (Frame) parent;
        } else if (parent != null) {
            parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        log.debug("Parent frame: {}", (parentFrame != null ? parentFrame.getTitle() : "null"));

        // Close any existing dialog first
        closeQRCodeDialog();

        // Create new dialog and store reference
        JDialog qrDialog = new JDialog(parentFrame, "Scan Payment QR Code", false);
        currentQrDialog = qrDialog;
        qrDialog.setLayout(new BorderLayout());
        qrDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        qrDialog.setPreferredSize(new Dimension(450, 400));

        log.debug("Adding QR code image to dialog");
        // Add QR code image
        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        qrLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        qrDialog.add(qrLabel, BorderLayout.CENTER);

        log.debug("Adding instructions to dialog");
        // Add instructions
        JLabel instructionLabel = new JLabel(
                "<html><div style='text-align: center; width: 200px;'>" +
                        "Please scan this QR code with your " +
                        order.getPaymentMethod().name() + " app</div></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        qrDialog.add(instructionLabel, BorderLayout.NORTH);

        log.debug("Adding cancel button to dialog");
        // Add cancel button
        JButton cancelButton = new JButton("Cancel Payment");
        cancelButton.setBackground(new java.awt.Color(220, 53, 69));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            log.debug("Cancel button clicked, disposing dialog");
            qrDialog.dispose();
            onCancel.accept(null);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttonPanel.add(cancelButton);
        qrDialog.add(buttonPanel, BorderLayout.SOUTH);

        log.debug("Preparing to display dialog");
        // Display dialog
        qrDialog.pack();
        qrDialog.setLocationRelativeTo(parent);
        // Don't set modal again, we already specified it in the constructor
        qrDialog.setResizable(false);

        log.debug("Setting dialog visible");
        qrDialog.setVisible(true);
        log.debug("Dialog should now be visible");

        // Add a check to see if the dialog is actually visible
        if (qrDialog.isVisible()) {
            log.debug("Dialog is confirmed visible");
        } else {
            log.debug("Dialog is NOT visible after setVisible(true)");
        }
    }
}

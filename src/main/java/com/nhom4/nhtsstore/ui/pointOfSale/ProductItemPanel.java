/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.nhom4.nhtsstore.ui.pointOfSale;
import com.nhom4.nhtsstore.common.ImageHelper;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;

import javax.swing.*;

/**
 *
 * @author Sang
 */
public class ProductItemPanel extends javax.swing.JPanel {
    private final NavigationService navigationService;
    private Product product;
    private SwingWorker<byte[], Void> imageLoader;

    public ProductItemPanel(Product product, NavigationService navigationService) {
        this.navigationService = navigationService;
        this.product = product;
        initComponents();

        // Set initial text content immediately
        lblProductName.setText(product.getName());
        lblSalePrice.setText(product.getSalePrice() + "$");
        lblQuantity.setText("Quantity: " + product.getQuantity() + "");
        lblProductImage.setIcon(new ImageIcon(getClass().getResource("/images/spinner.gif")));
        loadProductImageAsync();
    }

    private void loadProductImageAsync() {
        if (imageLoader != null && !imageLoader.isDone()) {
            imageLoader.cancel(true);
        }

        imageLoader = new SwingWorker<byte[], Void>() {
            @Override
            protected byte[] doInBackground() {
                if (!product.getImages().isEmpty()) {
                    return product.getImages().getFirst().getImageData();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    byte[] imageData = get();
                    if (!isCancelled()) {
                        ImageHelper.SetLabelImage(lblProductImage, 250, 100, imageData);
                    }
                } catch (Exception e) {
                    // Set default image on error
                    lblProductImage.setIcon(new ImageIcon(getClass().getResource("/images/No_Image_Available.jpg")));
                    e.printStackTrace();
                }
            }
        };

        imageLoader.execute();
    }

    // Cancel background tasks when panel is removed
    @Override
    public void removeNotify() {
        if (imageLoader != null && !imageLoader.isDone()) {
            imageLoader.cancel(true);
        }
        super.removeNotify();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblProductImage = new javax.swing.JLabel();
        lblSalePrice = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        lblProductName = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setForeground(new java.awt.Color(255, 255, 255));
        setAutoscrolls(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setFocusCycleRoot(true);
        setFocusTraversalPolicyProvider(true);
        setPreferredSize(new java.awt.Dimension(150, 100));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        lblProductImage.setBackground(new java.awt.Color(204, 204, 255));
        lblProductImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblSalePrice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSalePrice.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSalePrice.setText("Sale price");

        lblQuantity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblQuantity.setText("Quantity");

        lblProductName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblProductName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblProductName.setText("ProductName");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblQuantity))
                    .addComponent(lblProductName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblProductImage, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        RouteParams params = new RouteParams();
        params.set("product", product);
        navigationService.navigateTo(ProductDetailPanel.class, params);
    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblProductImage;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblSalePrice;
    // End of variables declaration//GEN-END:variables
}

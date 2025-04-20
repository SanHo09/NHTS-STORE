package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.ProductService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.utils.PanelManager;
import javax.swing.*;
import java.awt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author NamDang
 */
@Component
public class ProductEditPanel extends JPanel {
    private Product product;
    @Autowired
    private PanelManager panelManager;
    @Autowired
    private ApplicationState applicationState;
    @Autowired
    private ProductService service;
    
    public ProductEditPanel() {
        EventBus.getEntitySubject().subscribe(entity -> {
            this.product = (Product) entity;

            setLayout(new BorderLayout());

            // Panel chứa các trường nhập liệu
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Tạo các trường nhập liệu
            JTextField nameField = new JTextField(this.product != null ? this.product.getName() : "");
            JSpinner salePriceField = new JSpinner(this.product != null 
                                    ? new SpinnerNumberModel(this.product.getSalePrice(), 0, 1000, 1)
                                    : new SpinnerNumberModel(0, 0, 1000, 1));
            JSpinner purchasePriceField = new JSpinner(this.product != null 
                        ? new SpinnerNumberModel(this.product.getPurchasePrice(), 0, 1000, 1)
                        : new SpinnerNumberModel(0, 0, 1000, 1));
//            String[] categoryField = ;
            JSpinner quantityField = new JSpinner(this.product != null 
                                    ? new SpinnerNumberModel(this.product.getQuantity(), 0, 1000, 1)
                                    : new SpinnerNumberModel(0, 0, 1000, 1));
            JCheckBox activeCheck = new JCheckBox("", this.product != null && this.product.isActive());

            // Thêm các trường vào form
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);

            formPanel.add(new JLabel("Sale Price:"));
            formPanel.add(salePriceField);

            formPanel.add(new JLabel("Purchase Price:"));
            formPanel.add(purchasePriceField);
            
            formPanel.add(new JLabel("Quantity:"));
            formPanel.add(quantityField);
            
            formPanel.add(new JLabel("Active:"));
            formPanel.add(activeCheck);

            add(formPanel, BorderLayout.CENTER);

            // Panel chứa các nút
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            JButton deleteButton = new JButton("Delete");

            saveButton.addActionListener(e -> {
                try {
                    // Tạo hoặc cập nhật đối tượng product
                    Product updatedProduct = this.product != null ? 
                            this.product : new Product();
                    
                    updatedProduct.setName(nameField.getText());
                    updatedProduct.setQuantity((Integer) quantityField.getValue());
                    updatedProduct.setActive(activeCheck.isSelected());
                    
                    service.save(updatedProduct);
                    JOptionPane.showMessageDialog(this,
                            "Save successfully",
                            "Save Success", JOptionPane.INFORMATION_MESSAGE);
                    EventBus.postReload(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Error saving product: " + ex.getMessage(),
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            cancelButton.addActionListener(e -> {
                // Quay ve man hinh list
                JPanel listPanel = applicationState.getViewPanelByBean(ProductListPanel.class);
                this.panelManager.navigateTo(null, listPanel);
            });
            
            deleteButton.addActionListener(e -> {
                this.service.deleteById(this.product.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete product " + this.product.getName() + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                // Quay ve man hinh list
                JPanel listPanel = applicationState.getViewPanelByBean(ProductListPanel.class);
                this.panelManager.navigateTo(null, listPanel);
            });
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            if (this.product != null) {
                buttonPanel.add(deleteButton);
            }
            
            add(buttonPanel, BorderLayout.SOUTH);
        });
    }
}

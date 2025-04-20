package com.nhom4.nhtsstore.ui.base;

import com.nhom4.nhtsstore.entities.GenericEntity;
import com.nhom4.nhtsstore.entities.Product;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.*;

/**
 *
 * @author NamDang
 */
/**
 * Factory tạo form panel cho từng loại entity
 */
public class EntityFormPanelFactory {
    
    /**
     * Tạo form panel phù hợp với loại entity
     * @param entityClass Loại entity cần tạo form
     * @param entity Entity cần chỉnh sửa (null nếu là tạo mới)
     * @param saveCallback Callback được gọi khi lưu thành công
     * @return JPanel chứa form
     */
    public static JPanel createFormPanel(Class<?> entityClass, GenericEntity entity, Consumer<GenericEntity> saveCallback) {
        // Kiểm tra loại entity và gọi phương thức tương ứng
        if (entityClass.getSimpleName().equals("Product")) {
            return createProductFormPanel((Product) entity, saveCallback);
        }
//        else if (entityClass.getSimpleName().equals("Employee")) {
//            return createEmployeeFormPanel((Employee) entity, saveCallback);
//        }
        
        throw new IllegalArgumentException("No form available for entity type: " + entityClass.getSimpleName());
    }
    
    // Form cho Product
    private static JPanel createProductFormPanel(Product product, Consumer<GenericEntity> saveCallback) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel chứa các trường nhập liệu
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo các trường nhập liệu
        JTextField nameField = new JTextField(product != null ? product.getName() : "");
//        JTextArea descField = new JTextArea(product != null ? product.getDescription() : "", 3, 20);
//        JTextField priceField = new JTextField(product != null && product.getPrice() != null ? 
//                                            product.getPrice().toString() : "");
        JTextField quantityField = new JTextField(product != null ? String.valueOf(product.getQuantity()) : "0");
        JCheckBox activeCheck = new JCheckBox("", product != null && product.isActive());
        
        // Thêm các trường vào form
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        
//        formPanel.add(new JLabel("Description:"));
//        JScrollPane descScroll = new JScrollPane(descField);
//        formPanel.add(descScroll);
//        
//        formPanel.add(new JLabel("Price:"));
//        formPanel.add(priceField);
        
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        
        formPanel.add(new JLabel("Active:"));
        formPanel.add(activeCheck);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                // Tạo hoặc cập nhật đối tượng product
                Product updatedProduct = product != null ? 
                        product : new Product();
                
                updatedProduct.setName(nameField.getText());
//                updatedProduct.setDescription(descField.getText());
//                updatedProduct.setPrice(new java.math.BigDecimal(priceField.getText()));
                updatedProduct.setQuantity(Integer.parseInt(quantityField.getText()));
                updatedProduct.setActive(activeCheck.isSelected());
                
//                if (product == null) {
//                    updatedProduct.setCreatedDate(new java.util.Date());
//                }
                
                // Gọi callback để lưu
                saveCallback.accept(updatedProduct);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, 
                        "Error saving product: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> {
            // Tìm parent là JFrame và đóng lại
            Component component = panel;
            while (component != null && !(component instanceof JFrame)) {
                component = component.getParent();
            }
            
            if (component instanceof JFrame) {
                ((JFrame) component).dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
//    
//    // Form cho Employee (tương tự như Product)
//    private static JPanel createEmployeeFormPanel(com.example.management.model.Employee employee, Consumer<GenericEntity> saveCallback) {
//        // Cài đặt tương tự như product form
//        // Phương thức này sẽ được cài đặt khi có class Employee
//        JPanel panel = new JPanel();
//        panel.add(new JLabel("Employee form sẽ được cài đặt ở đây"));
//        return panel;
//    }
}

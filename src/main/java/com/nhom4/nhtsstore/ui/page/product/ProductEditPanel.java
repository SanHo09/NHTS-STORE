package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.services.CategoryService;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.ProductService;
import com.nhom4.nhtsstore.services.SupplierService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.components.DatePicker;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import org.jdatepicker.JDatePicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author NamDang
 */
@Scope("prototype")
@Component
public class ProductEditPanel extends JPanel implements RoutablePanel {
    private Product product;
    @Autowired
    private PanelManager panelManager;
    @Autowired
    private ApplicationState applicationState;
    @Autowired
    private ProductService productService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CategoryService categoryService;
    
    public ProductEditPanel() {}

    @Override
    public void onNavigate(RouteParams params) {
        this.product = params.get("entity", Product.class);
        initForm();
    }

    private void initForm() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Product Name
        JTextField nameField = new JTextField(product != null ? product.getName() : "");
        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(nameField);
        
        // Sale Price
        JSpinner salePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getSalePrice(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        formPanel.add(new JLabel("Sale Price:"));
        formPanel.add(salePriceField);
        
        // Purchase Price
        JSpinner purchasePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getPurchasePrice(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        formPanel.add(new JLabel("Purchase Price:"));
        formPanel.add(purchasePriceField);
        
        // Stock
        JSpinner quantityField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getQuantity(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(quantityField);
        
        // Active
        ToggleSwitch activeToggle = new ToggleSwitch();
        activeToggle.setSelected(product != null && product.isActive());
        formPanel.add(new JLabel("Status:"));
        formPanel.add(activeToggle);

        // Manufacturer
        JTextField manufacturerField = new JTextField(product != null ? product.getManufacturer() : "");
        formPanel.add(new JLabel("Manufacturer:"));
        formPanel.add(manufacturerField);

        // Manufacture Date
        DatePicker manufactureDatePicker = new DatePicker();
        manufactureDatePicker.setDate(product != null && product.getManufactureDate() != null
                ? product.getManufactureDate()
                : new Date());
        formPanel.add(new JLabel("Manufacture Date:"));
        formPanel.add(manufactureDatePicker);
        
        // Expiry Date
        JSpinner expiryDateField = new JSpinner(new SpinnerDateModel());
        expiryDateField.setEditor(new JSpinner.DateEditor(expiryDateField, "dd/MM/yyyy"));
        expiryDateField.setValue(product != null && product.getExpiryDate() != null
                ? product.getExpiryDate()
                : new Date());
        formPanel.add(new JLabel("Expiry Date:"));
        formPanel.add(expiryDateField);
        
        // Supplier
        List<Supplier> suppliers = supplierService.findAll();
        JComboBox<Supplier> supplierCombo = new JComboBox<>(suppliers.toArray(new Supplier[0]));
        if (product != null) supplierCombo.setSelectedItem(product.getSupplier());
        formPanel.add(new JLabel("Supplier:"));
        formPanel.add(supplierCombo);
        
        // Category
        List<Category> categories = categoryService.findAll();
        JComboBox<Category> categoryCombo = new JComboBox<>(categories.toArray(new Category[0]));
        if (product != null) categoryCombo.setSelectedItem(product.getCategory());
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton deleteButton = new JButton("Delete");
        
        // Select all value when focus
        UIUtils.applySelectAllOnFocus(nameField, salePriceField, purchasePriceField, manufacturerField, quantityField);
        
        saveButton.addActionListener(e -> {
            try {
                Product updatedProduct = product != null ? product : new Product();
                updatedProduct.setName(nameField.getText());
                updatedProduct.setSalePrice(((Number) salePriceField.getValue()).doubleValue());
                updatedProduct.setPurchasePrice(((Number) purchasePriceField.getValue()).doubleValue());
                updatedProduct.setManufacturer(manufacturerField.getText());
                updatedProduct.setManufactureDate(manufactureDatePicker.getDate());
                updatedProduct.setExpiryDate((Date) expiryDateField.getValue());
                updatedProduct.setSupplier((Supplier) supplierCombo.getSelectedItem());
                updatedProduct.setCategory((Category) categoryCombo.getSelectedItem());
                updatedProduct.setQuantity((Integer) quantityField.getValue());
                updatedProduct.setActive(activeToggle.isSelected());

                productService.save(updatedProduct);
                JOptionPane.showMessageDialog(this,
                        "Save successfully",
                        "Save Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                this.returnToList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error saving product: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> {
            this.returnToList();
        });

        deleteButton.addActionListener(e -> delete());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        if (product != null) {
            buttonPanel.add(deleteButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
    
    private void returnToList() {
        JPanel listPanel = applicationState.getViewPanelByBean(ProductListPanel.class);
        panelManager.navigateTo(null, listPanel);
    }
    
    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteById(product.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete product " + product.getName() + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                this.returnToList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting product: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

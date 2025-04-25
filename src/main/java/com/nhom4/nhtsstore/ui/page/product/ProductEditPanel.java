package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.ProductService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import javax.swing.*;
import java.awt.*;
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
    private ProductService service;
    
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

        JTextField nameField = new JTextField(product != null ? product.getName() : "");
        JSpinner salePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getSalePrice(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        JSpinner purchasePriceField = new JSpinner(product != null 
                    ? new SpinnerNumberModel(product.getPurchasePrice(), 0, 1000, 1)
                    : new SpinnerNumberModel(0, 0, 1000, 1));
        JSpinner quantityField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getQuantity(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        JCheckBox activeCheck = new JCheckBox("", product != null && product.isActive());

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton deleteButton = new JButton("Delete");

        saveButton.addActionListener(e -> {
            try {
                Product updatedProduct = product != null ? product : new Product();
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
            JPanel listPanel = applicationState.getViewPanelByBean(ProductListPanel.class);
            panelManager.navigateTo(null, listPanel);
        });

        deleteButton.addActionListener(e -> {
            service.deleteById(product.getId());
            JOptionPane.showMessageDialog(this,
                    "Delete product " + product.getName() + " successfully",
                    "Delete Success", JOptionPane.INFORMATION_MESSAGE);
            EventBus.postReload(true);
            JPanel listPanel = applicationState.getViewPanelByBean(ProductListPanel.class);
            panelManager.navigateTo(null, listPanel);
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        if (product != null) {
            buttonPanel.add(deleteButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
}

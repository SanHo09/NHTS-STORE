package com.nhom4.nhtsstore.ui.page.customer;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.services.impl.CustomerService;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.ui.base.GenericEditDialog;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@org.springframework.stereotype.Component
public class CustomerEditDialog extends JDialog implements GenericEditDialog<Customer> {
    @Autowired
    private CustomerService customerService;
    
    private Customer customer;
    private JTextField nameField, emailField, phoneField, addressField;
    private ToggleSwitch activeToggle;
    
    public CustomerEditDialog() {
        super((Frame) null, "", true);
    }
    
    private void initForm() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        activeToggle = new ToggleSwitch();

        if (customer != null) {
            nameField.setText(customer.getName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhoneNumber());
            addressField.setText(customer.getAddress());
            activeToggle.setSelected(customer.isActive());
        }
        
        int fieldWidth = 220;
        int row = 0;
        int column = 0;
        
        addFieldToForm(formPanel, createLabeledField("Name:", nameField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Email:", emailField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Phone number:", phoneField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Address:", addressField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Visible:", activeToggle, fieldWidth), gbc, column, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        // Select all value when focus
        UIUtils.applySelectAllOnFocus(nameField, emailField, phoneField, addressField);
        
        saveButton.addActionListener(e -> save());
        
        deleteButton.addActionListener(e -> {
            delete();
        });
        
        cancelButton.addActionListener(e -> {
            dispose();
        });

        buttonPanel.add(saveButton);
        if (customer != null) {
            buttonPanel.add(deleteButton);
        }
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        
        setLocationRelativeTo(null);
                
        revalidate();
        repaint();
    }

    private void save() {
       try {
           Customer updatedCustomer = customer != null ? customer : new Customer();
           updatedCustomer.setName(nameField.getText());
           updatedCustomer.setEmail(emailField.getText());
           updatedCustomer.setPhoneNumber(phoneField.getText());
           updatedCustomer.setAddress(addressField.getText());
           updatedCustomer.setActive(activeToggle.isSelected());

           customerService.save(updatedCustomer);
           JOptionPane.showMessageDialog(this,
                   "Save successfully",
                   "Save Success", JOptionPane.INFORMATION_MESSAGE);
           EventBus.postReload(true);
           dispose();
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, 
                   "Error saving customer: " + ex.getMessage(),
                   "Save Error", JOptionPane.ERROR_MESSAGE);
           ex.printStackTrace();
       }
   }
    
    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerService.deleteById(customer.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete customer " + "'" + customer.getName() + "'" + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting customer: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void showDialog(Customer customer) {
        this.customer = customer;
        initForm();
        setVisible(true);  // Show THIS dialog instance
    }
    
    private void addFieldToForm(JPanel formPanel, JPanel fieldPanel, GridBagConstraints gbc, int column, int row) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(fieldPanel, gbc);
    }

    private JPanel createLabeledField(String labelText, JComponent field, int width) {
        JPanel panel = new JPanel(new BorderLayout(10, 0)); // Giảm khoảng cách giữa label và field xuống 10px
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height)); // Đặt chiều rộng cố định cho label
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Không có khoảng cách
        field.setPreferredSize(new Dimension(width, field.getPreferredSize().height));
        rightPanel.add(field);

        panel.add(label, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }
}
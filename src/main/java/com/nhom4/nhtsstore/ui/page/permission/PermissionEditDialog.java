package com.nhom4.nhtsstore.ui.page.permission;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.impl.PermissionService;
import com.nhom4.nhtsstore.ui.base.GenericEditDialog;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@org.springframework.stereotype.Component
public class PermissionEditDialog extends JDialog implements GenericEditDialog<Permission> {
    @Autowired
    private PermissionService permissionService;
    
    private Permission permission;
    private JTextField nameField, descriptionField;
    private ToggleSwitch activeToggle;
    
    public PermissionEditDialog() {
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
        descriptionField = new JTextField();
        activeToggle = new ToggleSwitch();

        if (permission != null) {
            nameField.setText(permission.getPermissionName());
            descriptionField.setText(permission.getDescription());
            activeToggle.setSelected(permission.isActive());
        }
        
        int fieldWidth = 220;
        int row = 0;
        int column = 0;
        
        addFieldToForm(formPanel, createLabeledField("Name:", nameField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Description:", descriptionField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Visible:", activeToggle, fieldWidth), gbc, column, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        // Select all value when focus
        UIUtils.applySelectAllOnFocus(nameField, descriptionField);
        
        saveButton.addActionListener(e -> save());
        
        deleteButton.addActionListener(e -> {
            delete();
        });
        
        cancelButton.addActionListener(e -> {
            dispose();
        });

        buttonPanel.add(saveButton);
        if (permission != null) {
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
           Permission updatedPermission = permission != null ? permission : new Permission();
           updatedPermission.setPermissionName(nameField.getText());
           updatedPermission.setDescription(descriptionField.getText());
           updatedPermission.setActive(activeToggle.isSelected());

           permissionService.save(updatedPermission);
           JOptionPane.showMessageDialog(this,
                   "Save successfully",
                   "Save Success", JOptionPane.INFORMATION_MESSAGE);
           EventBus.postReload(true);
           dispose();
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, 
                   "Error saving permission: " + ex.getMessage(),
                   "Save Error", JOptionPane.ERROR_MESSAGE);
           ex.printStackTrace();
       }
   }
    
    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this permission?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                permissionService.deleteById(permission.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete permission " + "'" + permission.getPermissionName()+ "'" + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting permission: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
        
    @Override
    public void showDialog(Permission permission) {
        this.permission = permission;
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

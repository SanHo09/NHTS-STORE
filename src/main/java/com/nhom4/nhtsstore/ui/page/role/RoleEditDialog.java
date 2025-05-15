package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.RoleHasPermission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.impl.PermissionService;
import com.nhom4.nhtsstore.services.impl.RoleService;
import com.nhom4.nhtsstore.ui.base.GenericEditDialog;
import com.nhom4.nhtsstore.ui.shared.components.ComboBoxMultiSelection;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.UIUtils;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@org.springframework.stereotype.Component
public class RoleEditDialog extends JDialog implements GenericEditDialog<Role> {
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    private Role role;
    private JTextField nameField, descriptionField;
    private ToggleSwitch activeToggle;
    private ComboBoxMultiSelection<PermissionWrapper> permissionsCombo;
    
    public RoleEditDialog() {
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
        permissionsCombo = new ComboBoxMultiSelection<>();

        if (role != null) {
            nameField.setText(role.getRoleName());
            descriptionField.setText(role.getDescription());
            activeToggle.setSelected(role.isActive());
        }
        
        // Load permissions
        loadPermissions();
        
        int fieldWidth = 500;
        int row = 0;
        int column = 0;
        
        addFieldToForm(formPanel, createLabeledField("Name:", nameField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Description:", descriptionField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Visible:", activeToggle, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Permissions:", permissionsCombo, fieldWidth), gbc, column, row++);

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
        if (role != null) {
            buttonPanel.add(deleteButton);
        }
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set a reasonable size for the dialog
        setPreferredSize(new Dimension(700, 350));
        pack();
        
        setLocationRelativeTo(null);
                
        revalidate();
        repaint();
    }
    
    private void loadPermissions() {
        permissionsCombo.removeAllItems();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<Permission>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Permission> doInBackground() {
                return permissionService.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Permission> permissions = get();
                    for (Permission permission : permissions) {
                        permissionsCombo.addItem(new PermissionWrapper(
                                permission.getId(),
                                permission.getPermissionName()
                        ));
                    }

                    if (role != null && role.getRolePermissions() != null) {
                        loadRolePermissions();
                    }

                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(RoleEditDialog.this, 
                            "Error loading permissions: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void loadRolePermissions() {
        List<Object> selectedPermissions = new ArrayList<>();

        if (role.getRolePermissions() != null) {
            for (RoleHasPermission rhp : role.getRolePermissions()) {
                // Create a PermissionWrapper for each permission
                selectedPermissions.add(new PermissionWrapper(
                        rhp.getPermission().getId(),
                        rhp.getPermission().getPermissionName()
                ));
            }
        }

        permissionsCombo.setSelectedItems(selectedPermissions);
    }

    private void save() {
       try {
           Role updatedRole = role != null ? role : new Role();
           updatedRole.setRoleName(nameField.getText());
           updatedRole.setDescription(descriptionField.getText());
           updatedRole.setActive(activeToggle.isSelected());
           
           // Update permissions
           List<PermissionWrapper> selectedPermissions = permissionsCombo.getSelectedItems()
                   .stream()
                   .map(item -> (PermissionWrapper) item)
                   .toList();
           
           // Create or update role permissions
           Set<RoleHasPermission> rolePermissions = new HashSet<>();
           
           for (PermissionWrapper wrapper : selectedPermissions) {
               Permission permission = permissionService.findById(wrapper.getId());
               if (permission != null) {
                   RoleHasPermission rolePermission = RoleHasPermission.builder()
                           .role(updatedRole)
                           .permission(permission)
                           .build();
                   rolePermissions.add(rolePermission);
               }
           }
           
           if (updatedRole.getRolePermissions() == null) {
               updatedRole.setRolePermissions(new HashSet<>());
           } else {
               updatedRole.getRolePermissions().clear();
           }
           
           // Set the permissions
           updatedRole.getRolePermissions().addAll(rolePermissions);

           roleService.save(updatedRole);
           JOptionPane.showMessageDialog(this,
                   "Save successfully",
                   "Save Success", JOptionPane.INFORMATION_MESSAGE);
           EventBus.postReload(true);
           dispose();
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, 
                   "Error saving role: " + ex.getMessage(),
                   "Save Error", JOptionPane.ERROR_MESSAGE);
           ex.printStackTrace();
       }
   }
    
    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this role?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roleService.deleteById(role.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete role " + "'" + role.getRoleName()+ "'" + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting role: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
        
    @Override
    public void showDialog(Role role) {
        this.role = role;
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
    
    @Getter
    static class PermissionWrapper {
        private Long id;
        private String name;

        public PermissionWrapper(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            PermissionWrapper that = (PermissionWrapper) obj;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}

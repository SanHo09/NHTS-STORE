package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.RoleHasPermission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.impl.PermissionService;
import com.nhom4.nhtsstore.services.impl.RoleService;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.components.ComboBoxMultiSelection;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class RoleEditPanel extends JPanel implements RoutablePanel, LanguageManager.LanguageChangeListener {
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final NavigationService navigationService;
    private final LanguageManager languageManager;

    private Role role;
    private JTextField roleNameField;
    private JTextArea descriptionArea;
    private ToggleSwitch activeSwitch;
    private ComboBoxMultiSelection<PermissionWrapper> permissionsCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private boolean isNewRole = false;
    
    // UI components for localization
    private JLabel titleLabel;
    private JLabel roleNameLabel;
    private JLabel descriptionLabel;
    private JLabel activeLabel;
    private JLabel permissionsLabel;
    

    public RoleEditPanel(
            RoleService roleService, 
            PermissionService permissionService, 
            NavigationService navigationService,
            LanguageManager languageManager) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.navigationService = navigationService;
        this.languageManager = languageManager;

        setLayout(new BorderLayout(10, 10));
        initComponents();

    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        languageManager.addLanguageChangeListener(this);
        updateTexts();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        languageManager.removeLanguageChangeListener(this);
    }
    

    public void updateTexts() {
        SwingUtilities.invokeLater(() -> {
            // Update all text elements
            titleLabel.setText(languageManager.getText("role.edit.title"));
            roleNameLabel.setText(languageManager.getText("role.edit.name") + ":");
            descriptionLabel.setText(languageManager.getText("role.edit.description") + ":");
            activeLabel.setText(languageManager.getText("role.edit.active") + ":");
            permissionsLabel.setText(languageManager.getText("role.edit.permissions") + ":");
            saveButton.setText(languageManager.getText("role.edit.save"));
            cancelButton.setText(languageManager.getText("role.edit.cancel"));

            if (deleteButton != null) {
                deleteButton.setText(languageManager.getText("role.edit.delete"));
            }
        });

    }
    @Override
    public void onLanguageChanged() {
        updateTexts();
    }
    @Override
    public void onNavigate(RouteParams params) {
        if (params.get("entity") != null) {
            role = params.get("entity", Role.class);
            isNewRole = false;
            
            // Add delete button if not already added
            if (deleteButton == null) {
                deleteButton = new JButton(languageManager != null ? 
                    languageManager.getText("role.edit.delete") : "Delete");
                deleteButton.addActionListener(e -> deleteRole());
                
                JPanel buttonPanel = (JPanel) saveButton.getParent();
                buttonPanel.add(deleteButton, 0); // Add at beginning
                buttonPanel.revalidate();
                buttonPanel.repaint();
            }
        } else {
            role = new Role();
            isNewRole = true;
            
            // Remove delete button if it exists
            if (deleteButton != null) {
                JPanel buttonPanel = (JPanel) deleteButton.getParent();
                buttonPanel.remove(deleteButton);
                deleteButton = null;
                buttonPanel.revalidate();
                buttonPanel.repaint();
            }
        }

        loadPermissions();
        populateFields();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titleLabel = new JLabel(languageManager != null ? 
            languageManager.getText("role.edit.title") : "Role Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        roleNameLabel = new JLabel(languageManager != null ? 
            languageManager.getText("role.edit.name") + ":" : "Role Name:");
        formPanel.add(roleNameLabel, gbc);

        gbc.gridy++;
        descriptionLabel = new JLabel(languageManager != null ? 
            languageManager.getText("role.edit.description") + ":" : "Description:");
        formPanel.add(descriptionLabel, gbc);

        gbc.gridy++;
        activeLabel = new JLabel(languageManager != null ? 
            languageManager.getText("role.edit.active") + ":" : "Active:");
        formPanel.add(activeLabel, gbc);

        gbc.gridy++;
        permissionsLabel = new JLabel(languageManager != null ? 
            languageManager.getText("role.edit.permissions") + ":" : "Permissions:");
        formPanel.add(permissionsLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        roleNameField = new JTextField(20);
        formPanel.add(roleNameField, gbc);

        gbc.gridy++;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        gbc.gridy++;
        activeSwitch = new ToggleSwitch();
        activeSwitch.setSelected(true);
        formPanel.add(activeSwitch, gbc);

        gbc.gridy++;
        permissionsCombo = new ComboBoxMultiSelection<>();
        formPanel.add(permissionsCombo, gbc);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton(languageManager != null ? 
            languageManager.getText("role.edit.save") : "Save");
        saveButton.addActionListener(e -> saveRole());

        cancelButton = new JButton(languageManager != null ? 
            languageManager.getText("role.edit.cancel") : "Cancel");
        cancelButton.addActionListener(e -> navigateBack());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void navigateBack() {
        navigationService.navigateTo(RoleListPanel.class, new RouteParams());
    }
    
    private void deleteRole() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this role?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    roleService.deleteById(role.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Check for exceptions
                        EventBus.postReload(true);
                        navigateBack();
                        
                        Toast.show(RoleEditPanel.this, Toast.Type.SUCCESS,
                                "Role deleted successfully",
                                ToastLocation.TOP_CENTER);
                    } catch (Exception e) {
                        Toast.show(RoleEditPanel.this, Toast.Type.ERROR,
                                "Error deleting role: " + e.getMessage(),
                                ToastLocation.TOP_CENTER);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };
            worker.execute();
        }
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

                    if (!isNewRole && role != null) {
                        loadRolePermissions();
                    }

                } catch (InterruptedException | ExecutionException e) {
                    Toast.show(RoleEditPanel.this, Toast.Type.ERROR,
                            "Error loading permissions: " + e.getMessage(),
                            ToastLocation.TOP_CENTER);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void populateFields() {
        if (!isNewRole && role != null) {
            roleNameField.setText(role.getRoleName());
            descriptionArea.setText(role.getDescription());
            activeSwitch.setSelected(role.isActive());
        }
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

    private void saveRole() {
        if (!validateForm()) {
            return;
        }

        // Update role object
        role.setRoleName(roleNameField.getText().trim());
        role.setDescription(descriptionArea.getText().trim());
        role.setActive(activeSwitch.isSelected());

        // Get selected permissions
        List<PermissionWrapper> selectedPermissions = permissionsCombo.getSelectedItems()
                .stream()
                .map(item -> (PermissionWrapper) item)
                .toList();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        saveButton.setEnabled(false);

        SwingWorker<Role, Void> worker = new SwingWorker<>() {
            @Override
            protected Role doInBackground() throws Exception {
                // Create or update role permissions
                Set<RoleHasPermission> rolePermissions = new HashSet<>();

                for (PermissionWrapper wrapper : selectedPermissions) {
                    Permission permission = permissionService.findById(wrapper.getId());
                    if (permission != null) {
                        RoleHasPermission rolePermission = RoleHasPermission.builder()
                                .role(role)
                                .permission(permission)
                                .build();
                        rolePermissions.add(rolePermission);
                    }
                }
                if (role.getRolePermissions() == null) {
                    role.setRolePermissions(new HashSet<>());
                } else {
                    role.getRolePermissions().clear();
                }
                // Set the permissions
                role.getRolePermissions().addAll(rolePermissions);

                return roleService.save(role);
            }

            @Override
            protected void done() {
                try {
                    Role savedRole = get();
                    Toast.show(RoleEditPanel.this, Toast.Type.SUCCESS,
                            "Role saved successfully!",
                            ToastLocation.TOP_CENTER);

                    EventBus.postReload(true);
                    navigateBack();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.show(RoleEditPanel.this, Toast.Type.ERROR,
                            "Error saving role: " + e.getMessage(),
                            ToastLocation.TOP_CENTER);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    saveButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private boolean validateForm() {
        if (roleNameField.getText().trim().isEmpty()) {
            Toast.show(this, Toast.Type.WARNING, "Role name is required", ToastLocation.TOP_CENTER);
            roleNameField.requestFocus();
            return false;
        }
        return true;
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
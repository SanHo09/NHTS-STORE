package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.RoleHasPermission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.PermissionService;
import com.nhom4.nhtsstore.services.RoleService;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
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
public class RoleEditPanel extends JPanel implements RoutablePanel {
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final NavigationService navigationService;

    private Role role;
    private JTextField roleNameField;
    private JTextArea descriptionArea;
    private ToggleSwitch activeSwitch;
    private ComboBoxMultiSelection<PermissionWrapper> permissionsCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isNewRole = false;

    public RoleEditPanel(RoleService roleService, PermissionService permissionService, NavigationService navigationService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.navigationService = navigationService;

        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    @Override
    public void onNavigate(RouteParams params) {
        if (params.get("entity") != null) {
            role = params.get("entity", Role.class);
            isNewRole = false;
        } else {
            role = new Role();
            isNewRole = true;
        }

        loadPermissions();
        populateFields();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Role Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        formPanel.add(new JLabel("Role Name:"), gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Active:"), gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Permissions:"), gbc);

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

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveRole());

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> navigateBack());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void navigateBack() {
        navigationService.navigateTo(RoleListPanel.class, new RouteParams());
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
                role.getRolePermissions().clear();
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
                } catch (InterruptedException | ExecutionException ex) {
                    Toast.show(RoleEditPanel.this, Toast.Type.ERROR,
                            "Error saving role: " + ex.getMessage(),
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
            Toast.show(this, Toast.Type.WARNING,
                    "Role name is required",
                    ToastLocation.TOP_CENTER);
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
package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.entities.rbac.RoleHasPermission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.IRoleService;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.components.ComboBoxMultiSelection;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class RoleEditPanel extends JPanel implements RoutablePanel {
    private final IRoleService roleService;
    private final NavigationService navigationService;

    private Role role;
    private JTextField roleNameField;
    private JTextArea descriptionArea;
    private ToggleSwitch activeSwitch;
    private ComboBoxMultiSelection<PermissionWrapper> permissionsCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isNewRole = false;

    public RoleEditPanel(IRoleService roleService, NavigationService navigationService) {
        this.roleService = roleService;
        this.navigationService = navigationService;

        setLayout(new BorderLayout(10, 10));

        initComponents();
    }
    @Override
    public void onNavigate(RouteParams params) {
        if (params.get("entity") != null) {
            role =  params.get("entity",Role.class);
            isNewRole = false;
        } else {
            role = new Role();
            isNewRole = true;
        }
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
        loadPermissions();
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

        //dummy data
        permissionsCombo.addItem(new PermissionWrapper(1L, "CREATE_USER"));
        permissionsCombo.addItem(new PermissionWrapper(2L, "EDIT_USER"));
        permissionsCombo.addItem(new PermissionWrapper(3L, "DELETE_USER"));
        permissionsCombo.addItem(new PermissionWrapper(4L, "VIEW_USER"));
    }

    private void populateFields() {
        if (!isNewRole) {
            roleNameField.setText(role.getRoleName());
            descriptionArea.setText(role.getDescription());
            activeSwitch.setSelected(role.isActive());


            loadRolePermissions();
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


        try {
//            roleService.save(role)
            JOptionPane.showMessageDialog(this,
                    "Role saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);


            EventBus.postReload(true);

            navigateBack();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving role: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (roleNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Role name is required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
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
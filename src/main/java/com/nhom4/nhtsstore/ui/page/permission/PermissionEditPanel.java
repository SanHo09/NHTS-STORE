package com.nhom4.nhtsstore.ui.page.permission;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.PermissionService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

@Scope(value = "prototype")
@Controller
public class PermissionEditPanel extends JPanel implements RoutablePanel {
    private final PermissionService permissionService;
    private final NavigationService navigationService;

    private Permission permission;
    private boolean isEditMode = false;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JCheckBox activeCheckbox;
    private JButton saveButton;
    private JButton cancelButton;

    public PermissionEditPanel(PermissionService permissionService, NavigationService navigationService) {
        this.permissionService = permissionService;
        this.navigationService = navigationService;
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        nameField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        activeCheckbox = new JCheckBox("Active");
        activeCheckbox.setSelected(true);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Permission name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Permission Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Active checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        formPanel.add(activeCheckbox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to main panel
        add(new JLabel("Permission Details", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        saveButton.addActionListener(e -> savePermission());
        cancelButton.addActionListener(e -> navigationService.navigateTo(AppView.PERMISSION,new RouteParams()));
    }

    private void savePermission() {
        if (!validateForm()) {
            return;
        }

        SwingWorker<Permission, Void> worker = new SwingWorker<>() {
            @Override
            protected Permission doInBackground() {
                if (permission == null) {
                    permission = new Permission();
                }

                permission.setPermissionName(nameField.getText().trim());
                permission.setDescription(descriptionArea.getText().trim());
                permission.setActive(activeCheckbox.isSelected());

                return permissionService.save(permission);
            }

            @Override
            protected void done() {
                try {
                    Permission savedPermission = get();
                    Toast.show(PermissionEditPanel.this, Toast.Type.SUCCESS,
                            "Permission saved successfully",
                            ToastLocation.TOP_CENTER);

                    // Notify other components to refresh
                    EventBus.postReload(true);

                    navigationService.navigateTo(PermissionListPanel.class,new RouteParams());
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(
                            PermissionEditPanel.this,
                            "Error saving permission: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            Toast.show(this, Toast.Type.WARNING,
                    "Permission name cannot be empty",
                    ToastLocation.TOP_CENTER);
            return false;
        }

        if (name.length() > 50) {
            Toast.show(this, Toast.Type.WARNING,
                    "Permission name cannot exceed 50 characters",
                    ToastLocation.TOP_CENTER);
            return false;
        }

        return true;
    }

    private void loadPermissionData() {
        if (permission != null) {
            nameField.setText(permission.getPermissionName());
            descriptionArea.setText(permission.getDescription());
            activeCheckbox.setSelected(permission.isActive());
        }
    }

    @Override
    public void onNavigate(RouteParams params) {
        if (params.get("entity") != null) {
            permission = params.get("entity", Permission.class);
            isEditMode = true;
            loadPermissionData();
        } else {
            Long permissionId = params.get("permissionId", Long.class);
            isEditMode = (permissionId != null);

            if (isEditMode) {
                SwingWorker<Permission, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Permission doInBackground() {
                        return permissionService.findById(permissionId);
                    }

                    @Override
                    protected void done() {
                        try {
                            permission = get();
                            if (permission != null) {
                                loadPermissionData();
                            } else {
                                EventBus.postReload(true);
                                Toast.show(PermissionEditPanel.this, Toast.Type.ERROR,
                                        "Permission not found",
                                        ToastLocation.TOP_CENTER);
                                navigationService.navigateTo(AppView.PERMISSION,new RouteParams());
                            }
                        } catch (Exception e) {
                            Toast.show(PermissionEditPanel.this, Toast.Type.ERROR,
                                    "Error loading permission: " + e.getMessage(),
                                    ToastLocation.TOP_CENTER);
                            navigationService.navigateTo(AppView.PERMISSION,new RouteParams());
                        }
                    }
                };
                worker.execute();
            } else {
                permission = null;
                nameField.setText("");
                descriptionArea.setText("");
                activeCheckbox.setSelected(true);
            }
        }
    }
}
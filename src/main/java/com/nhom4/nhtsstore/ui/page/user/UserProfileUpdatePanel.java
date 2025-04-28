package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.services.RoleService;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleVmWrapper;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import java.awt.*;
import java.util.*;
@Scope("prototype")
@Controller
public class UserProfileUpdatePanel extends JPanel implements RoutablePanel {
    private final IUserService userService;
    private final RoleService roleService;
    private final ApplicationState appState;
    private final ValidationHelper validationHelper;
    private final NavigationService navigationService;
    private UserDetailVm userDetailVm;
    @Getter
    private final String modalId = "userProfileEditModal";


    private JTextField txtFullName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JFXPanel passwordPanel;
    private MFXPasswordField txtPassword;
    private JComboBox<RoleVmWrapper> cmbRoles;
    private JPanel boxRoles;
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnGeneratePassword;
    private JPanel formPanel;

    public UserProfileUpdatePanel(UserService userService, RoleService roleService, ApplicationState appState,
                                  ValidationHelper validationHelper, NavigationService navigationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.appState = appState;
        this.validationHelper = validationHelper;
        this.navigationService = navigationService;

        setLayout(new BorderLayout());
        initComponents();
        setPreferredSize(new Dimension(491, getPreferredSize().height));
        setMinimumSize(new Dimension(491, 250)); // Minimum height for scrolling
    }

    private void initComponents() {
        // Initialize form components
        txtFullName = new JTextField();
        txtUsername = new JTextField();
        txtEmail = new JTextField();
        txtUsername.setEditable(false);
        // Setup roles component
        cmbRoles = new JComboBox<>();
        cmbRoles.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RoleVmWrapper) {
                    setText(formatRoleName(((RoleVmWrapper) value).getRoleVm().getRoleName()));
                }
                return this;
            }
        });

        boxRoles = new JPanel(new BorderLayout());
        boxRoles.add(cmbRoles, BorderLayout.CENTER);

        // Setup buttons
        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());

        // Create form layout
        formPanel = new JPanel(new MigLayout("fillx, insets 10", "[100]20[grow]", ""));

        // Add basic components
        formPanel.add(new JLabel("Full name:"), "");
        formPanel.add(txtFullName, "growx, wrap");

        formPanel.add(new JLabel("Username:"), "");
        formPanel.add(txtUsername, "growx, wrap");

        formPanel.add(new JLabel("Email:"), "");
        formPanel.add(txtEmail, "growx, wrap");

        // Add the form panel to the main panel
        add(formPanel, BorderLayout.CENTER);
    }

    private String formatRoleName(String roleName) {
        if (roleName == null || roleName.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        for (String word : roleName.split("_")) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    private void setupPasswordField() {
        passwordPanel = new JFXPanel();
        btnGeneratePassword = new JButton("Generate");
        btnGeneratePassword.addActionListener(e -> generateRandomPassword());

        Platform.runLater(() -> {
            MFXPasswordField passwordField = new MFXPasswordField();
            passwordField.setFloatingText("Password");
            passwordField.setPrefWidth(350);
            passwordField.setPrefHeight(30);
            passwordField.setShowPassword(true);
            passwordField.setFloatMode(FloatMode.DISABLED);
            Scene scene = new Scene(new StackPane(passwordField), Color.TRANSPARENT);
            passwordPanel.setScene(scene);
            txtPassword = passwordField;
        });
    }

    private void generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        Platform.runLater(() -> txtPassword.setText(sb.toString()));
    }

    private void onSave() {
        RoleVmWrapper wrapper = (RoleVmWrapper) cmbRoles.getSelectedItem();
        RoleVm role = RoleVm.builder().build();
        if(wrapper != null) {
            role = wrapper.getRoleVm();
        }else {
            RoleWithPermissionVm roleWithPermission =userDetailVm.getRole();
            role.setRoleId(roleWithPermission.getId());
        }

        UserUpdateVm updateVm = UserUpdateVm.builder()
                .userId(userDetailVm.getUserId())
                .fullName(txtFullName.getText())
                .email(txtEmail.getText())
                .role(role)
                .build();

        if (txtPassword != null) {
            String password = txtPassword.getText();
            if (!password.isBlank()) {
                updateVm.setPassword(password);
            }
        }

        if (handleUpdateProfile(updateVm)) {
            RouteParams params = new RouteParams();
            params.set("userId", userDetailVm.getUserId());
            navigationService.navigateTo(AppView.USER_PROFILE, params);
            ModalDialog.closeModal(getModalId());
        }
    }

    private void onCancel() {
        ModalDialog.closeModal(getModalId());
    }

    private boolean handleUpdateProfile(UserUpdateVm vm) {
        if (!validationHelper.validateAndCollectErrors(vm).isEmpty()) {
            return false;
        }

        try {
            userService.editProfile(vm);
            Toast.show(this, Toast.Type.SUCCESS, "Profile updated successfully", ToastLocation.TOP_CENTER);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.show(this, Toast.Type.ERROR, "Update failed: " + e.getMessage(), ToastLocation.TOP_CENTER);
            return false;
        }
    }

    @Override
    public void onNavigate(RouteParams params) {
        UserDetailVm user = params.get("user", UserDetailVm.class);
        if (user != null) {
            this.userDetailVm = user;
            setFormData();
        }
    }

    private void setFormData() {
        if (userDetailVm == null) return;

        User userSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get roles from application state
        boolean isSuperAdmin = appState.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.equals("SUPER_ADMIN"));
        boolean isSelf = userSession.getUserId().equals(userDetailVm.getUserId());
        // Check role from the single role object
        boolean targetHasSuperAdminRole = userDetailVm.getRole() != null &&
                userDetailVm.getRole().getRoleName().equals("SUPER_ADMIN");

        boolean showRoles = (isSuperAdmin && !isSelf) || (!isSuperAdmin && isSelf);
        boolean showPassword = isSuperAdmin && !isSelf && !targetHasSuperAdminRole;
        boolean enableEditing = isSuperAdmin || isSelf;

        // Clear form and rebuild
        formPanel.removeAll();

        // Add basic fields that always show
        formPanel.add(new JLabel("Full name:"), "");
        formPanel.add(txtFullName, "growx, wrap");

        formPanel.add(new JLabel("Username:"), "");
        formPanel.add(txtUsername, "growx, wrap");

        formPanel.add(new JLabel("Email:"), "");
        formPanel.add(txtEmail, "growx, wrap");

        // Setup data
        txtFullName.setText(userDetailVm.getFullName());
        txtUsername.setText(userDetailVm.getUsername());
        txtEmail.setText(userDetailVm.getEmail());
        txtFullName.setEditable(enableEditing);
        txtEmail.setEditable(enableEditing);

        // Add roles if needed
        if (showRoles) {
            cmbRoles.removeAllItems();
            DefaultComboBoxModel<RoleVmWrapper> model = new DefaultComboBoxModel<>();

            Set<RoleVm> allRoles = roleService.getAllRoles();
            for (RoleVm role : allRoles) {
                model.addElement(new RoleVmWrapper(role));
            }
            cmbRoles.setModel(model);

            // Select current role if exists
            if (userDetailVm.getRole() != null) {
                RoleWithPermissionVm currentRole = userDetailVm.getRole();
                RoleVm roleVm = new RoleVm();
                roleVm.setRoleId(currentRole.getId());
                roleVm.setRoleName(currentRole.getRoleName());

                for (int i = 0; i < model.getSize(); i++) {
                    RoleVmWrapper wrapper = model.getElementAt(i);
                    if (wrapper.getRoleVm().getRoleId().equals(roleVm.getRoleId())) {
                        cmbRoles.setSelectedItem(wrapper);
                        break;
                    }
                }
            }

            formPanel.add(new JLabel("Role:"), "");
            formPanel.add(boxRoles, "growx, wrap");
        }

        // Add password if needed
        if (showPassword) {
            setupPasswordField();
            formPanel.add(new JLabel("Password:"), "");
            formPanel.add(passwordPanel, "growx, h 40, wrap");
            formPanel.add(new JLabel(""), "");
            formPanel.add(btnGeneratePassword, "wrap");
        }

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        formPanel.add(buttonPanel, "span 2, growx, wrap");

        formPanel.revalidate();
        formPanel.repaint();
    }
}
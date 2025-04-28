package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.services.RoleService;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleVmWrapper;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
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
import org.springframework.stereotype.Controller;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import java.awt.*;
import java.util.*;
@Scope("prototype")
@Controller
public class UserCreatePanel extends JPanel implements RoutablePanel {
    private final IUserService userService;
    private final RoleService roleService;
    private final ValidationHelper validationHelper;
    private final NavigationService navigationService;
    @Getter
    private final String modalId = "userCreateModal";

    // Form components
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

    public UserCreatePanel(UserService userService, RoleService roleService,
                           ValidationHelper validationHelper, NavigationService navigationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.validationHelper = validationHelper;
        this.navigationService = navigationService;

        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Initialize form components
        txtFullName = new JTextField();
        txtUsername = new JTextField();
        txtEmail = new JTextField();

        // Setup password field
        setupPasswordField();

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
        btnSave = new JButton("Create");
        btnCancel = new JButton("Cancel");
        btnGeneratePassword = new JButton("Generate");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());
        btnGeneratePassword.addActionListener(e -> generateRandomPassword());

        // Create form layout
        formPanel = new JPanel(new MigLayout("fillx, insets 10", "[100]20[grow]", ""));

        // Add components to form
        formPanel.add(new JLabel("Full name:"), "");
        formPanel.add(txtFullName, "growx, wrap");

        formPanel.add(new JLabel("Username:"), "");
        formPanel.add(txtUsername, "growx, wrap");

        formPanel.add(new JLabel("Email:"), "");
        formPanel.add(txtEmail, "growx, wrap");

        formPanel.add(new JLabel("Role:"), "");
        formPanel.add(boxRoles, "growx, wrap");

        formPanel.add(new JLabel("Password:"), "");
        formPanel.add(passwordPanel, "growx, h 40, wrap");

        formPanel.add(new JLabel(""), "");
        formPanel.add(btnGeneratePassword, "wrap");

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        formPanel.add(buttonPanel, "span 2, growx, wrap");

        // Add the form panel to the main panel
        add(formPanel, BorderLayout.CENTER);

        // Load roles on initialization
        loadRoles();
    }

    private void setupPasswordField() {
        passwordPanel = new JFXPanel();

        Platform.runLater(() -> {
            MFXPasswordField passwordField = new MFXPasswordField();
            passwordField.setFloatingText("Password");
            passwordField.setPrefWidth(300);
            passwordField.setPrefHeight(30);
            passwordField.setShowPassword(true);
            passwordField.setFloatMode(FloatMode.DISABLED);

            Scene scene = new Scene(new StackPane(passwordField), Color.TRANSPARENT);
            passwordPanel.setScene(scene);
            txtPassword = passwordField;
        });
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

    private void generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        Platform.runLater(() -> txtPassword.setText(sb.toString()));
    }

    private void loadRoles() {
        cmbRoles.removeAllItems();
        DefaultComboBoxModel<RoleVmWrapper> model = new DefaultComboBoxModel<>();

        Set<RoleVm> allRoles = roleService.getAllRoles();
        for (RoleVm role : allRoles) {
            model.addElement(new RoleVmWrapper(role));
        }
        cmbRoles.setModel(model);

        // Select first role by default if available
        if (model.getSize() > 0) {
            cmbRoles.setSelectedIndex(0);
        }
    }

    private void onSave() {
        if (cmbRoles.getSelectedItem() == null) {
            Toast.show(this, Toast.Type.ERROR, "Please select a role", ToastLocation.TOP_CENTER);
            return;
        }

        RoleVmWrapper wrapper = (RoleVmWrapper) cmbRoles.getSelectedItem();
        RoleVm role = wrapper.getRoleVm();

        // Get password from JavaFX component
        String password = "";
        try {
            password = txtPassword.getText();
        } catch (Exception ignored) {}

        if (password.isEmpty()) {
            Toast.show(this, Toast.Type.ERROR, "Password is required", ToastLocation.TOP_CENTER);
            return;
        }

        UserCreateVm createVm = UserCreateVm.builder()
                .fullName(txtFullName.getText())
                .username(txtUsername.getText())
                .email(txtEmail.getText())
                .password(password)
                .role(RoleVm.builder().roleId(role.getRoleId()).build())
                .build();

        if (handleCreateUser(createVm)) {
            ModalDialog.closeModal(getModalId());
        }
    }

    private void onCancel() {
        ModalDialog.closeModal(getModalId());
    }

    private boolean handleCreateUser(UserCreateVm vm) {
        if (!validationHelper.validateAndCollectErrors(vm).isEmpty()) {
            return false;
        }

        try {
            userService.createUser(vm);
            Toast.show(this, Toast.Type.SUCCESS, "User created successfully", ToastLocation.TOP_CENTER);

            // Refresh users list
//            navigationService.navigateTo(AppView.USER);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.show(this, Toast.Type.ERROR, "Creation failed: " + e.getMessage(), ToastLocation.TOP_CENTER);
            return false;
        }
    }

    @Override
    public void onNavigate(RouteParams params) {
        // Reset form fields when navigating to this panel
        txtFullName.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        Platform.runLater(() -> txtPassword.setText(""));

        // Reload roles
        loadRoles();
    }
}
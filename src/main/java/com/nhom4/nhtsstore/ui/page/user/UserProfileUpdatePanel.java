package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.services.impl.RoleService;
import com.nhom4.nhtsstore.services.impl.UserService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.role.RoleVm;
import com.nhom4.nhtsstore.viewmodel.role.RoleVmWrapper;
import com.nhom4.nhtsstore.viewmodel.role.RoleWithPermissionVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
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

@Controller
@Scope("prototype")
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
    private JTextField txtPassword;
    private JComboBox<RoleVmWrapper> cmbRoles;
    private JPanel boxRoles;
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnGeneratePassword;
    private JPanel formPanel;
    private JPanel buttonPanel;
    
    // Active status toggle
    private ToggleSwitch toggleActive;
    
    public UserProfileUpdatePanel(UserService userService, RoleService roleService, ApplicationState appState,
                                  ValidationHelper validationHelper, NavigationService navigationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.appState = appState;
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
        txtUsername.setEditable(false);
        txtPassword = new JTextField();
        
        // Setup active status toggle
        toggleActive = new ToggleSwitch();
        
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
        btnGeneratePassword = new JButton("Generate");
        btnGeneratePassword.addActionListener(e -> generateRandomPassword());
        
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());

        // Create form layout
        formPanel = new JPanel(new MigLayout("fillx, insets 10", "[100]20[grow]", ""));

        // Buttons panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        
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

    private void generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        txtPassword.setText(sb.toString());
    }

    private void onSave() {
        RoleVmWrapper wrapper = (RoleVmWrapper) cmbRoles.getSelectedItem();
        RoleVm role = RoleVm.builder().build();
        if(wrapper != null) {
            role = wrapper.getRoleVm();
        }else {
            RoleWithPermissionVm roleWithPermission = userDetailVm.getRole();
            role.setRoleId(roleWithPermission.getId());
        }

        UserUpdateVm updateVm = UserUpdateVm.builder()
                .userId(userDetailVm.getUserId())
                .fullName(txtFullName.getText())
                .email(txtEmail.getText())
                .role(role)
                .active(toggleActive.isSelected())
                .build();

        // Get password if provided
        String password = txtPassword.getText();
        if (!password.isBlank()) {
            updateVm.setPassword(password);
        }

        if (handleUpdateProfile(updateVm)) {
            RouteParams params = new RouteParams();
            params.set("userId", userDetailVm.getUserId());
            if (checkIsInModal()){
                navigationService.navigateTo(AppView.USER_PROFILE, params);
                ModalDialog.closeModal(getModalId());
            }else {
                navigationService.navigateBack();
            }
        }
    }

    private void onCancel() {
        if (checkIsInModal()) {
            ModalDialog.closeModal(getModalId());
        } else {
            navigationService.navigateBack();
        }
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
        if (user == null) {
            Long userId = params.get("userId", Long.class);
            if (userId != null) {
                user = userService.findUserById(userId);
            }
        }
        if (user != null) {
            this.userDetailVm = user;
            setFormData();
        }
    }

    private void setFormData() {
        if (userDetailVm == null) return;

        User userSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get roles from application state
        boolean isSuperAdmin = appState.getCurrentUser().getRole().equals("SUPER_ADMIN");
        boolean isSelf = userSession.getUserId().equals(userDetailVm.getUserId());

        boolean targetHasSuperAdminRole = userDetailVm.getRole() != null &&
                userDetailVm.getRole().getRoleName().equals("SUPER_ADMIN");

        boolean showRoles = ((isSuperAdmin && !isSelf) || (!isSuperAdmin && isSelf)) &&
                (userDetailVm.getRole() != null || isSuperAdmin);
        boolean showPassword = isSuperAdmin && !isSelf && !targetHasSuperAdminRole;
        boolean enableEditing = isSuperAdmin || isSelf;
        formPanel.removeAll();

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
        toggleActive.setSelected(userDetailVm.isActive());
        
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
            formPanel.add(new JLabel("Password:"), "");
            formPanel.add(txtPassword, "growx, wrap");
            formPanel.add(new JLabel(""), "");
            formPanel.add(btnGeneratePassword, "wrap");
        }
        
        // Add active status toggle
        //super admin can edit active status of other users but not their own
       boolean showActiveStatus = (isSuperAdmin && !isSelf) ;
        if (showActiveStatus) {
            JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            togglePanel.add(toggleActive);
            togglePanel.add(new JLabel("  Active"));
            toggleActive.setEnabled(enableEditing);

            formPanel.add(new JLabel("Status:"), "");
            formPanel.add(togglePanel, "growx, wrap");


        }
        formPanel.add(buttonPanel, "span 2, growx, wrap");
        formPanel.revalidate();
        formPanel.repaint();
    }
    
    private boolean checkIsInModal() {
        return ModalDialog.isIdExist(this.modalId);
    }
}

package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.common.FieldValidationError;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.FieldErrorUtil;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import com.nhom4.nhtsstore.viewmodel.user.UserUpdateVm;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserProfileEditPanel extends JPanel implements RoutablePanel {
    private final UserService userService;
    private final ApplicationState appState;
    private final ValidationHelper validationHelper;
    private JFXPanel jfxPanel;
    private UserDetailVm userDetailVm;
    UserProfileEditPanel(UserService userService, ApplicationState appState, ValidationHelper validationHelper) {
        this.userService = userService;
        this.appState = appState;
        this.validationHelper = validationHelper;
        SwingUtilities.invokeLater(() -> {
            jfxPanel = JavaFxSwing.createJFXPanelFromFxml(
                    "/fxml/UserEditProfileForm.fxml",
                    appState.getApplicationContext()
            );
            add(jfxPanel, BorderLayout.CENTER);
        });
    }
    @FXML public MFXPasswordField txtPassword;
    @FXML public MFXComboBox comboRole;
    @FXML public MFXTextField txtUsername;
    @FXML public MFXTextField txtEmail;
    @FXML public MFXTextField txtFullName;
    @Getter
    private final String modalId = "userProfileEditModal";

    private boolean handleUpdateProfile(UserUpdateVm vm){
        if (!handleValidation(vm)) {
            return false;
        }
        try {
            userService.editProfile(vm);
            Toast.show(this,
                    Toast.Type.SUCCESS,
                    "Update profile successfully",
                    ToastLocation.TOP_CENTER);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.show(this,
                    Toast.Type.ERROR,
                    "Update profile failed",
                    ToastLocation.TOP_CENTER);
            return false;
        }

    }
    private <Vm> boolean handleValidation(Vm vm) {
        List<FieldValidationError> errors = validationHelper.validateAndCollectErrors(vm);
        if (!errors.isEmpty()) {
//            Map<String, MFXTextField> fieldMap = new HashMap<>();
//
//            FieldErrorUtil.showErrorTooltip(errors,fieldMap);

            return false;
        }
        return true;
    }

    @FXML
    public void onActionCancel(ActionEvent actionEvent) {
        ModalDialog.closeModal(this.modalId);
    }
    @FXML
    public void onActionSave(ActionEvent actionEvent) {
        UserUpdateVm userUpdateVm = UserUpdateVm.builder()
                .userId(userDetailVm.getUserId())
                .username(txtUsername.getText())
                .email(txtEmail.getText())
                .fullName(txtFullName.getText())
                .build();
        boolean isValid = handleUpdateProfile(userUpdateVm);
        if (isValid) {
            ModalDialog.closeModal(this.modalId);
            clearForm();
        }
    }
    private void clearForm() {
        txtUsername.clear();
        txtEmail.clear();
        txtFullName.clear();
        txtPassword.clear();
    }
    @Override
    public void onNavigate(RouteParams params) {
        UserDetailVm userDetailVm = params.get("user", UserDetailVm.class);
        if (userDetailVm != null) {
            this.userDetailVm = userDetailVm;
            setFormData(userDetailVm);
        }
    }
    private void setFormData(UserDetailVm userDetailVm) {
        txtUsername.setText(userDetailVm.getUsername());
        txtEmail.setText(userDetailVm.getEmail());
        txtFullName.setText(userDetailVm.getFullName());

    }
}

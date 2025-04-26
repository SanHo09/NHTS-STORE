package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.common.FieldValidationError;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.FieldErrorUtil;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.user.UserChangePasswordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
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
public class UserChangePasswordPanel extends JPanel implements RoutablePanel {
    private final UserService userService;
    private final ApplicationState appState;
    private final ValidationHelper validationHelper;
    @FXML public MFXPasswordField txtFieldNewPass;
    @FXML public MFXPasswordField txtCurrentPass;
    @FXML public MFXPasswordField txtFieldConfirmPass;
    @FXML public MFXButton btnUpdate;
    @FXML public MFXButton btnCancel;
    private UserDetailVm userDetailVm;
    @Getter
    private final String modalId = "userChangePasswordModal";
    private JFXPanel jfxPanel;

    UserChangePasswordPanel (UserService userService, ApplicationState appState, ValidationHelper validationHelper) {
        this.userService = userService;
        this.appState = appState;
        this.validationHelper = validationHelper;
        SwingUtilities.invokeLater(() -> {
            jfxPanel = JavaFxSwing.createJFXPanelFromFxml(
                    "/fxml/UserChangePasswordForm.fxml",
                    appState.getApplicationContext()
            );
            add(jfxPanel, BorderLayout.CENTER);
        });

    }


    @Override
    public void onNavigate(RouteParams params) {
        UserDetailVm userDetailVm = params.get("user", UserDetailVm.class);
        if (userDetailVm != null) {
            this.userDetailVm = userDetailVm;
        }
    }
    private boolean handleChangePassword(UserChangePasswordVm vm){
        if (!handleValidation(vm)) {
            return false;
        }
        try {
             userService.changePassword(vm);
             Toast.show(this,
                     Toast.Type.SUCCESS,
                     "Change password successfully",
                     ToastLocation.TOP_CENTER);
                return true;
         }catch (IllegalArgumentException e) {
             Toast.show(this,
                     Toast.Type.WARNING,
                     e.getMessage(),
                     ToastLocation.TOP_CENTER);
                return false;
         }

    }
    private void clearForm() {
        txtCurrentPass.clear();
        txtFieldNewPass.clear();
        txtFieldConfirmPass.clear();
    }
    private <Vm> boolean handleValidation(Vm vm) {
        List<FieldValidationError> errors = validationHelper.validateAndCollectErrors(vm);
        if (!errors.isEmpty()) {
            Map<String, MFXTextField> fieldMap = new HashMap<>();
            fieldMap.put("password", txtCurrentPass);
            fieldMap.put("newPassword", txtFieldNewPass);
            fieldMap.put("confirmPassword", txtFieldConfirmPass);
            FieldErrorUtil.showErrorTooltip(errors,fieldMap);

            return false;
        }
        return true;
    }
    @FXML
    public void onActionCancel(ActionEvent actionEvent) {
        ModalDialog.closeModal(this.modalId);
        clearForm();
    }
    @FXML
    public void onActionUpdate(ActionEvent actionEvent) {
        UserChangePasswordVm userChangePasswordVm = UserChangePasswordVm.builder()
                .userId(userDetailVm.getUserId())
                .password(txtCurrentPass.getText())
                .newPassword(txtFieldNewPass.getText())
                .confirmPassword(txtFieldConfirmPass.getText())
                .build();

        boolean isValid = handleChangePassword(userChangePasswordVm);
        if (isValid) {
            ModalDialog.closeModal(this.modalId);
            clearForm();
        }
    }


}

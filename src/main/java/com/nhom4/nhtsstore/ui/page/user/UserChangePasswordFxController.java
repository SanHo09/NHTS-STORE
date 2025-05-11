package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.common.FieldValidationError;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.base.LocalizableComponent;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.FieldErrorUtil;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
import com.nhom4.nhtsstore.utils.ValidationHelper;
import com.nhom4.nhtsstore.viewmodel.user.UserChangePasswordVm;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import org.springframework.stereotype.Controller;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Controller
public class UserChangePasswordFxController implements Initializable {
    private final IUserService userService;
    private final ValidationHelper validationHelper;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    
    @FXML public MFXPasswordField txtFieldNewPass;
    @FXML public MFXPasswordField txtCurrentPass;
    @FXML public MFXPasswordField txtFieldConfirmPass;
    @FXML public MFXButton btnUpdate;
    @FXML public MFXButton btnCancel;
    @FXML public Parent rootPane;
    
    private UserDetailVm userDetailVm;
    private JComponent parent;
    private Consumer<Boolean> onUpdateAction;
    private Runnable onCancelAction;

    public UserChangePasswordFxController(IUserService userService, 
                                        ValidationHelper validationHelper,
                                        ThemeManager themeManager,
                                        LanguageManager languageManager) {
        this.userService = userService;
        this.validationHelper = validationHelper;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up theme listener
        JavaFxThemeUtil.setupThemeListener(rootPane, themeManager);
        
        // Update texts with current language
        updateTexts();
        
        // Listen for language changes
        languageManager.addLanguageChangeListener(this::updateTexts);
    }
    
    public void setParent(JComponent parent) {
        this.parent = parent;
    }
    
    public void setUserDetailVm(UserDetailVm userDetailVm) {
        this.userDetailVm = userDetailVm;
    }
    
    public void setOnUpdateAction(Consumer<Boolean> onUpdateAction) {
        this.onUpdateAction = onUpdateAction;
    }
    
    public void setOnCancelAction(Runnable onCancelAction) {
        this.onCancelAction = onCancelAction;
    }
    
    public void updateTexts() {
        Platform.runLater(()->{
            try {
                if (txtCurrentPass != null) {
                    txtCurrentPass.setFloatingText(languageManager.getText("user.current_password"));
                }
                if (txtFieldNewPass != null) {
                    txtFieldNewPass.setFloatingText(languageManager.getText("user.new_password"));
                }
                if (txtFieldConfirmPass != null) {
                    txtFieldConfirmPass.setFloatingText(languageManager.getText("user.confirm_password"));
                }
                if (btnCancel != null) {
                    btnCancel.setText(languageManager.getText("user.cancel"));
                }
                if (btnUpdate != null) {
                    btnUpdate.setText(languageManager.getText("user.save"));
                }
            } catch (Exception e) {
                System.err.println("Error updating texts: " + e.getMessage());
            }
        });
    }
    
    private boolean handleChangePassword(UserChangePasswordVm vm){
        if (!handleValidation(vm)) {
            return false;
        }
        try {
            userService.changePassword(vm);
            Toast.show(parent,
                    Toast.Type.SUCCESS,
                    languageManager.getText("user.password_changed_success"),
                    ToastLocation.TOP_CENTER);
            return true;
        } catch (IllegalArgumentException e) {
            Toast.show(parent,
                    Toast.Type.WARNING,
                    e.getMessage(),
                    ToastLocation.TOP_CENTER);
            return false;
        }
    }
    
    public void clearForm() {
        Platform.runLater(() -> {
            txtCurrentPass.clear();
            txtFieldNewPass.clear();
            txtFieldConfirmPass.clear();
        });
    }
    
    private <Vm> boolean handleValidation(Vm vm) {
        List<FieldValidationError> errors = validationHelper.validateAndCollectErrors(vm);
        if (!errors.isEmpty()) {
            Map<String, MFXTextField> fieldMap = new HashMap<>();
            fieldMap.put("password", txtCurrentPass);
            fieldMap.put("newPassword", txtFieldNewPass);
            fieldMap.put("confirmPassword", txtFieldConfirmPass);
            FieldErrorUtil.showErrorTooltip(errors, fieldMap);
            return false;
        }
        return true;
    }
    
    @FXML
    public void onActionCancel(ActionEvent actionEvent) {
        if (onCancelAction != null) {
            onCancelAction.run();
        }
    }
    
    @FXML
    public void onActionUpdate(ActionEvent actionEvent) {
        if (userDetailVm == null) {
            return;
        }
        
        UserChangePasswordVm userChangePasswordVm = UserChangePasswordVm.builder()
                .userId(userDetailVm.getUserId())
                .password(txtCurrentPass.getText())
                .newPassword(txtFieldNewPass.getText())
                .confirmPassword(txtFieldConfirmPass.getText())
                .build();

        boolean isValid = handleChangePassword(userChangePasswordVm);
        if (onUpdateAction != null) {
            onUpdateAction.accept(isValid);
        }
    }
} 
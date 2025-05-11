package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Controller
public class UserProfileFxController implements Initializable {

    private final ThemeManager themeManager;
    private final LanguageManager languageManager;

    // UI components
    @FXML public MFXButton btnChangePassword;
    @FXML public MFXButton btnEditProfile;
    @FXML public Label lblFullName;
    @FXML public Label lblEmail;
    @FXML public Label lblUsername;
    @FXML public Label lblRole;
    @FXML public Parent rootPane;
    
    // Field labels
    @FXML public Label lblProfileTitle;
    @FXML public Label lblFullNameLabel;
    @FXML public Label lblEmailLabel;
    @FXML public Label lblUsernameLabel;
    @FXML public Label lblRoleLabel;
    
    // Callbacks for actions
    private Consumer<ActionEvent> changePasswordAction;
    private Consumer<ActionEvent> editProfileAction;
    
    // User data
    private UserDetailVm userDetailVm;

    public UserProfileFxController(ThemeManager themeManager, LanguageManager languageManager) {
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
    
    public void updateTexts() {
        Platform.runLater(() -> {
            try {
                // Update buttons
                if (btnChangePassword != null) {
                    btnChangePassword.setText(languageManager.getText("user.change_password"));
                }
                if (btnEditProfile != null) {
                    btnEditProfile.setText(languageManager.getText("user.edit_profile"));
                }

                // Update field labels
                if (lblProfileTitle != null) {
                    lblProfileTitle.setText(languageManager.getText("user.profile"));
                }
                if (lblFullNameLabel != null) {
                    lblFullNameLabel.setText(languageManager.getText("user.fullname"));
                }
                if (lblEmailLabel != null) {
                    lblEmailLabel.setText(languageManager.getText("user.email"));
                }
                if (lblUsernameLabel != null) {
                    lblUsernameLabel.setText(languageManager.getText("user.username"));
                }
                if (lblRoleLabel != null) {
                    lblRoleLabel.setText(languageManager.getText("user.role"));
                }
            } catch (Exception e) {
                System.err.println("Error updating texts: " + e.getMessage());
            }
        });
    }
    
    public void updateUserData(UserDetailVm userDetailVm) {
        this.userDetailVm = userDetailVm;
        Platform.runLater(() -> {
            if (lblFullName != null) lblFullName.setText(userDetailVm.getFullName());
            if (lblEmail != null) lblEmail.setText(userDetailVm.getEmail());
            if (lblUsername != null) lblUsername.setText(userDetailVm.getUsername());
            if (lblRole != null) lblRole.setText(userDetailVm.getRole()!= null ? userDetailVm.getRole().getRoleName() : "N/A");
        });
    }
    
    @FXML
    public void onActionChangePassword(ActionEvent actionEvent) {
        if (changePasswordAction != null) {
            changePasswordAction.accept(actionEvent);
        }
    }
    
    @FXML
    public void onActionEditProfile(ActionEvent actionEvent) {
        if (editProfileAction != null) {
            editProfileAction.accept(actionEvent);
        }
    }
    
    // Setters for action callbacks
    public void setChangePasswordAction(Consumer<ActionEvent> action) {
        this.changePasswordAction = action;
    }
    
    public void setEditProfileAction(Consumer<ActionEvent> action) {
        this.editProfileAction = action;
    }
    
    public UserDetailVm getUserDetailVm() {
        return userDetailVm;
    }
} 
package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import raven.modal.ModalDialog;

import javax.swing.*;
import java.awt.*;

@Controller
public class UserChangePasswordPanel extends JPanel implements RoutablePanel, LanguageManager.LanguageChangeListener {
    private final IUserService userService;
    private final ApplicationState appState;
    private final ThemeManager themeManager;
    private final UserChangePasswordFxController userChangePasswordFxController;
    
    private UserDetailVm userDetailVm;
    @Getter
    private final String modalId = "userChangePasswordModal";
    private JFXPanel jfxPanel;

    UserChangePasswordPanel(IUserService userService, 
                           ApplicationState appState,
                           ThemeManager themeManager,
                           UserChangePasswordFxController userChangePasswordFxController) {
        this.userService = userService;
        this.appState = appState;
        this.themeManager = themeManager;
        this.userChangePasswordFxController = userChangePasswordFxController;
        
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        Platform.runLater(() -> {
            // Create JavaFX panel with separate controller
            jfxPanel = JavaFxSwing.createJFXPanelFromFxml(
                    "/fxml/UserChangePasswordForm.fxml",
                    appState.getApplicationContext()
            );
            
            // Set the parent for toast notifications
            userChangePasswordFxController.setParent(this);
            
            // Configure actions
            userChangePasswordFxController.setOnUpdateAction(isValid -> {
                if (isValid) {
                    ModalDialog.closeModal(this.modalId);
                    userChangePasswordFxController.clearForm();
                }
            });
            
            userChangePasswordFxController.setOnCancelAction(() -> {
                ModalDialog.closeModal(this.modalId);
                userChangePasswordFxController.clearForm();
            });
            
            add(jfxPanel, BorderLayout.CENTER);
        });
    }

    @Override
    public void onNavigate(RouteParams params) {
        UserDetailVm userDetailVm = params.get("user", UserDetailVm.class);
        if (userDetailVm != null) {
            this.userDetailVm = userDetailVm;
            userChangePasswordFxController.setUserDetailVm(userDetailVm);
        }
    }

    @Override
    public void onLanguageChanged() {
        Platform.runLater(userChangePasswordFxController::updateTexts);

    }
}

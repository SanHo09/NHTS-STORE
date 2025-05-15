package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.SwingThemeUtil;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import org.springframework.stereotype.Controller;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.Option;

import javax.swing.*;
import java.awt.*;

@Controller
public class UserProfilePanel extends JPanel implements RoutablePanel, LanguageManager.LanguageChangeListener {
    private final ApplicationState appState;
    private final IUserService userService;
    private final UserChangePasswordPanel userChangePasswordPanel;
    private final UserProfileUpdatePanel userProfileUpdatePanel;
    private final ThemeManager themeManager;
    private final UserProfileFxController userProfileFxController;
    private final LanguageManager languageManager;
    private JFXPanel jfxPanel;
    private UserDetailVm userDetailVm;

    UserProfilePanel(ApplicationState appState,
                     IUserService userService,
                     UserChangePasswordPanel userChangePasswordPanel,
                     UserProfileUpdatePanel userProfileUpdatePanel,
                     ThemeManager themeManager,
                     UserProfileFxController userProfileFxController, LanguageManager languageManager) {
        this.appState = appState;
        this.userService = userService;
        this.userChangePasswordPanel = userChangePasswordPanel;
        this.userProfileUpdatePanel = userProfileUpdatePanel;
        this.themeManager = themeManager;
        this.userProfileFxController = userProfileFxController;
        this.languageManager = languageManager;
        setLayout(new BorderLayout());
        initComponent();
    }

    private void initComponent() {
        Platform.runLater(() -> {
            jfxPanel = JavaFxSwing.createJFXPanelFromFxml(
                    "/fxml/UserProfilePage.fxml",
                    appState.getApplicationContext()
            );
            
            // Set action handlers for buttons
            userProfileFxController.setChangePasswordAction(this::handleChangePassword);
            userProfileFxController.setEditProfileAction(this::handleEditProfile);
            
            add(jfxPanel, BorderLayout.CENTER);
        });
    }

    @Override
    public void onNavigate(RouteParams params) {

        Platform.runLater(() -> {
            try {
                User user= params.get("entity", User.class);
                Long userId = params.get("userId", Long.class);
                Long finalUserId;
                if (user != null) {
                    finalUserId = user.getUserId();
                } else if (userId != null) {
                    finalUserId = userId;
                } else {
                    finalUserId = appState.getCurrentUser().getUserId();
                }
                // Load and display user data
                if (finalUserId != null) {
                    userDetailVm = userService.findUserById(finalUserId);
                    if (userDetailVm != null) {
                        userProfileFxController.updateUserData(userDetailVm);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to load user data: " + e.getMessage());
            }
        });

    }


    private void handleChangePassword(ActionEvent actionEvent) {
        Option option = ModalDialog.createOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        option.getLayoutOption().setMovable(true);
        RouteParams params = new RouteParams();
        params.set("user", this.userDetailVm);
        userChangePasswordPanel.onNavigate(params);
        SimpleModalBorder simpleModalBorder= new SimpleModalBorder(userChangePasswordPanel,
                languageManager.getText("user.change_password"));
        SwingThemeUtil.applyThemeAndListenForChanges(
                simpleModalBorder,
                themeManager
        );
        ModalDialog.showModal(this, simpleModalBorder, option, userChangePasswordPanel.getModalId());
    }
    
    private void handleEditProfile(ActionEvent actionEvent) {
        Option option = ModalDialog.createOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        option.getLayoutOption().setMovable(true);
        RouteParams params = new RouteParams();
        params.set("user", this.userDetailVm);
        userProfileUpdatePanel.onNavigate(params);

        SimpleModalBorder simpleModalBorder= new SimpleModalBorder(userProfileUpdatePanel,
                languageManager.getText("user.edit_profile"));
        SwingThemeUtil.applyThemeAndListenForChanges(
                simpleModalBorder,
                themeManager
        );
        ModalDialog.showModal(this, simpleModalBorder, option, userProfileUpdatePanel.getModalId());
    }

    @Override
    public void onLanguageChanged() {
        Platform.runLater(userProfileFxController::updateTexts);
    }
}
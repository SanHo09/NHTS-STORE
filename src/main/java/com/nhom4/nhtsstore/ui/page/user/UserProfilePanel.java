package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.Option;
import javax.swing.*;
import java.awt.*;
@Controller
public class UserProfilePanel extends JPanel implements RoutablePanel {
    private final ApplicationState appState;
    private final IUserService userService;
    private final UserChangePasswordPanel userChangePasswordPanel;
    private final UserProfileUpdatePanel userProfileUpdatePanel;
    @FXML public MFXButton btnChangePassword;
    @FXML public MFXButton btnEditProfile;
    private JFXPanel jfxPanel;
    private UserDetailVm userDetailVm;
    @FXML public Label lblFullName;
    @FXML public Label lblEmail;
    @FXML public Label lblUsername;
    @FXML public Label lblRole;

    UserProfilePanel(ApplicationState appState, IUserService userService, UserChangePasswordPanel userChangePasswordPanel, UserProfileUpdatePanel userProfileUpdatePanel) {
        this.appState = appState;
        this.userService = userService;
        this.userChangePasswordPanel = userChangePasswordPanel;
        this.userProfileUpdatePanel = userProfileUpdatePanel;
        setLayout(new BorderLayout());
    }

    @PostConstruct
    private void initComponent() {
        new JFXPanel();
        jfxPanel = JavaFxSwing.createJFXPanelFromFxml(
                "/fxml/UserProfilePage.fxml",
                appState.getApplicationContext()
        );
        add(jfxPanel, BorderLayout.CENTER);
    }

    @Override
    public void onNavigate(RouteParams params) {
        Long userId = params.get("userId", Long.class);
        if (userId != null) {
            loadUserById(userId);
        }
    }

    private void loadUserById(Long userId) {

        SwingWorker<UserDetailVm,Void> worker = new SwingWorker<>() {
            @Override
            protected UserDetailVm doInBackground() {
                return userService.findUserById(userId);
            }

            @Override
            protected void done() {
                try {
                    userDetailVm = get();
                    updateUserDataFields(userDetailVm);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(UserProfilePanel.this, "Failed to load user data", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    public void updateUserDataFields(UserDetailVm user) {
        Platform.runLater(() -> {
            lblFullName.setText(user.getFullName());
            lblEmail.setText(user.getEmail());
            lblUsername.setText(user.getUsername());

            lblRole.setText(user.getRole().getRoleName());
        });
    }
    @FXML
    public void onActionChangePassword(ActionEvent actionEvent) {
        Option option = ModalDialog.createOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        option.getLayoutOption().setMovable(true);
        RouteParams params = new RouteParams();
        params.set("user", this.userDetailVm);
        userChangePasswordPanel.onNavigate(params);
        SimpleModalBorder simpleModalBorder= new SimpleModalBorder(userChangePasswordPanel,
                "Change password");
        ModalDialog.showModal(this,simpleModalBorder, option,userChangePasswordPanel.getModalId());


    }
    @FXML
    public void onActionEditProfile(ActionEvent actionEvent) {
        Option option = ModalDialog.createOption();
        option.setBackgroundClickType(Option.BackgroundClickType.BLOCK);
        option.getLayoutOption().setMovable(true);
        RouteParams params = new RouteParams();
        params.set("user", this.userDetailVm);
        userProfileUpdatePanel.onNavigate(params);
        SimpleModalBorder simpleModalBorder= new SimpleModalBorder(userProfileUpdatePanel,
                "Edit profile");
        ModalDialog.showModal(this,simpleModalBorder, option, userProfileUpdatePanel.getModalId());
    }
}
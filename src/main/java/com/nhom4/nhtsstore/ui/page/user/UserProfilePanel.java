package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.services.UserService;
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
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.Option;
import javax.swing.*;
import java.awt.*;

@Controller
public class UserProfilePanel extends JPanel implements RoutablePanel {
    private final ApplicationState appState;
    private final UserService userService;
    private final UserChangePasswordPanel userChangePasswordPanel;
    private final UserProfileEditPanel userProfileEditPanel;
    @FXML public MFXButton btnChangePassword;
    @FXML public MFXButton btnEditProfile;
    private JFXPanel jfxPanel;
    private UserDetailVm userDetailVm;
    @FXML public Label lblFullName;
    @FXML public Label lblEmail;
    @FXML public Label lblUsername;
    @FXML public Label lblRole;

    UserProfilePanel(ApplicationState appState, UserService userService, UserChangePasswordPanel userChangePasswordPanel, UserProfileEditPanel userProfileEditPanel) {
        this.appState = appState;
        this.userService = userService;
        this.userChangePasswordPanel = userChangePasswordPanel;
        this.userProfileEditPanel = userProfileEditPanel;
        setLayout(new BorderLayout());
    }

    @PostConstruct
    private void initComponent() {
        Platform.runLater(() -> {
            jfxPanel = JavaFxSwing.createJFXPanelFromFxml(
                    "/fxml/UserProfilePage.fxml",
                    appState.getApplicationContext()
            );
            add(jfxPanel, BorderLayout.CENTER);
        });
    }

    @Override
    public void onNavigate(RouteParams params) {
        Integer userId = params.get("userId", Integer.class);
        if (userId != null) {
            loadUserById(userId);
        }
    }

    private void loadUserById(Integer userId) {
        JavaFxSwing.runAndWait(() -> {
            this.userDetailVm = userService.findUserById(userId);
            if (this.userDetailVm != null) {
                updateUserDataFields(this.userDetailVm);
            }
        });

    }
    public void updateUserDataFields(UserDetailVm user) {
        lblFullName.setText(user.getFullName());
        lblEmail.setText(user.getEmail());
        lblUsername.setText(user.getUsername());
        StringBuilder stringBuilder = new StringBuilder();
        user.getRoles().forEach(role -> {
            stringBuilder.append(role.getRoleName());
            if (user.getRoles().size() > 1) {
                stringBuilder.append(", ");
            }
        });
        lblRole.setText(stringBuilder.toString());
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
        userProfileEditPanel.onNavigate(params);
        SimpleModalBorder simpleModalBorder= new SimpleModalBorder(userProfileEditPanel,
                "Edit profile");
        ModalDialog.showModal(this,simpleModalBorder, option,userProfileEditPanel.getModalId());
    }
}
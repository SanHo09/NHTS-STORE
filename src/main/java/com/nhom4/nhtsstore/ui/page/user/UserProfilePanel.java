package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.viewmodel.user.UserDetailVm;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;

@Controller
public class UserProfilePanel extends JPanel implements RoutablePanel {
    private final ApplicationState appState;
    private final UserService userService;
    private JFXPanel jfxPanel;

    @FXML public Label lblFullName;
    @FXML public Label lblEmail;
    @FXML public Label lblUsername;
    @FXML public Label lblRole;

    public UserProfilePanel(ApplicationState appState, UserService userService) {
        this.appState = appState;
        this.userService = userService;
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
            UserDetailVm user = userService.findUserById(userId);
            if (user != null) {
                updateUserData(user);
            }
        });

    }
    public void updateUserData(UserDetailVm user) {
        lblFullName.setText(user.getFullName());
        lblEmail.setText(user.getEmail());
        lblUsername.setText(user.getUsername());
        lblRole.setText(user.getRoles().toString());
    }
}
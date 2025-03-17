package com.nhom4.nhtsstore.ui.login;

import com.nhom4.nhtsstore.ui.ApplicationState;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class LoginFrame extends JFrame {
    private final LoginPanel loginPanel;
    private final ApplicationState appState;

    LoginFrame(LoginPanel loginPanel, ApplicationState appState) {
        this.loginPanel = loginPanel;
        this.appState = appState;


    }
    @PostConstruct
    private void initDialog() {
        setTitle("Login to NHTS Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        add(loginPanel, BorderLayout.CENTER);

        // Listen for authentication state changes
        appState.authenticatedProperty().addListener((obs, wasAuthenticated, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                if (isAuthenticated) {
                    setVisible(false);
                    dispose();

                }
            });
        });
    }

    public void showLogin() {
        setVisible(true);
    }
}
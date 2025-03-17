package com.nhom4.nhtsstore.ui.login;

import com.nhom4.nhtsstore.entities.User;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class LoginPanel extends JPanel {
    private  JTextField usernameField;
    private  JPasswordField passwordField;
    private  JButton loginButton;
    private  JLabel statusLabel;

    private final ApplicationState appState;
    private final UserService userService;
    public LoginPanel(ApplicationState appState, UserService userService) {
        this.appState = appState;
        this.userService = userService;
    }
    @PostConstruct
    private void createUIComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create components
        JLabel titleLabel = new JLabel("NHTS Store Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        gbc.gridy = 4;
        add(statusLabel, gbc);

        // Add action listener to login button
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty");
            return;
        }

        // Authenticate user
        if (userService.authenticate(username, password)) {
            // Login successful - get the user object for the session
            UserSessionVm user = userService.findByUsername(username);
            appState.login(user);
            statusLabel.setText("");
            clearFields();
        } else {
            // Login failed
            statusLabel.setText("Invalid username or password");
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
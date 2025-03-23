package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.ApplicationState;
//import com.nhom4.nhtsstore.ui.customComponent.IconButton;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component
public class ApplicationHeader extends JPanel {
    private final ApplicationState applicationState;
    private JLabel userInfoLabel;
    private JButton logoutButton;

    public ApplicationHeader(ApplicationState applicationState) {
        this.applicationState = applicationState;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        setPreferredSize(new Dimension(0, 60));

        initComponents();

        // Update user info when authentication changes
        applicationState.authenticatedProperty().addListener((obs, oldVal, newVal) -> {
            updateUserInfo();
        });
    }

    private void initComponents() {
        // App title on the left
        JLabel titleLabel = new JLabel("NHTS Store");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        add(titleLabel, BorderLayout.WEST);

        // User info and controls on the right
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        // Search bar
        JTextField searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        userPanel.add(searchField);

        // User info
        userInfoLabel = new JLabel("Guest User");
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        userPanel.add(userInfoLabel);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setVisible(false);
        logoutButton.addActionListener(this::handleLogout);
        userPanel.add(logoutButton);

        add(userPanel, BorderLayout.EAST);

        updateUserInfo();
    }

    private void updateUserInfo() {
        boolean isAuthenticated = applicationState.authenticatedProperty().get();
        String username = isAuthenticated ? applicationState.getCurrentUser().getUsername() : "Guest User";

        userInfoLabel.setText(username);
        logoutButton.setVisible(isAuthenticated);
    }

    private void handleLogout(ActionEvent e) {
        applicationState.logout();
    }
}
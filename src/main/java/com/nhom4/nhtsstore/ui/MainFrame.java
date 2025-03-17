package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.repositories.SupplierRepository;
import com.nhom4.nhtsstore.ui.login.LoginFrame;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import javax.swing.*;

@Component
public class MainFrame extends JFrame {
    private final MainPanel mainPanel;
    private final SupplierRepository supplierRepository;
    private final ApplicationState appState;
    private final LoginFrame loginFrame;

    MainFrame(MainPanel mainPanel, SupplierRepository supplierRepository,
              ApplicationState appState, LoginFrame loginFrame) {
        this.mainPanel = mainPanel;
        this.supplierRepository = supplierRepository;
        this.appState = appState;
        this.loginFrame = loginFrame;
    }

    @PostConstruct
    private void init() {
        setTitle("NHTS Store");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(mainPanel);

        // Start hidden initially
        setVisible(false);

        // Listen for authentication state changes
        appState.authenticatedProperty().addListener((obs, wasAuthenticated, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                setVisible(isAuthenticated);
            });
        });

        // Show login dialog
        SwingUtilities.invokeLater(loginFrame::showLogin);
    }
}
package com.nhom4.nhtsstore.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.repositories.SupplierRepository;
import com.nhom4.nhtsstore.ui.login.LoginFrame;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import raven.modal.Toast;

import javax.swing.*;

@Controller
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

        // This is like watcher in Vue.js or useEffect in React - it listens for changes in the authentication state
        // If the user is authenticated, the main frame is shown
        appState.authenticatedProperty().addListener((obs, wasAuthenticated, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                setVisible(isAuthenticated);

            });
        });

        // Show login frame first
        SwingUtilities.invokeLater(() -> {
            FlatIntelliJLaf.setup();
            loginFrame.setVisible(true);

        });

    }
}
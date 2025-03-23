package com.nhom4.nhtsstore.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.repositories.SupplierRepository;
import com.nhom4.nhtsstore.ui.login.LoginPanel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import java.awt.*;

@Controller
public class MainFrame extends JFrame {
    private final MainPanel mainPanel;
    private final SupplierRepository supplierRepository;
    private final ApplicationState appState;
    private final LoginPanel loginPanel;

    MainFrame(MainPanel mainPanel, SupplierRepository supplierRepository,
              ApplicationState appState, LoginPanel loginPanel) {
        this.mainPanel = mainPanel;
        this.supplierRepository = supplierRepository;
        this.appState = appState;
        this.loginPanel = loginPanel;
    }

    @PostConstruct
    private void init() {
        setTitle("NHTS Store");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        setMaximizedBounds(ge.getMaximumWindowBounds());
//        setLocationRelativeTo(null);
        // Start hidden initially


        // This is like watcher in Vue.js or useEffect in React - it listens for changes in the authentication state
        // If the user is authenticated, the main frame is shown
        appState.authenticatedProperty().addListener((obs, wasAuthenticated, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                setupLayout();
                setVisible(isAuthenticated);

            });
        });

        // Show login frame first
        SwingUtilities.invokeLater(() -> {
            FlatIntelliJLaf.setup();
            loginPanel.setVisible(true);

        });
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }
}
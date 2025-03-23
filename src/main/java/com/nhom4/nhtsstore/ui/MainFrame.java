package com.nhom4.nhtsstore.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.repositories.SupplierRepository;
import com.nhom4.nhtsstore.ui.login.LoginPanel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;

@Controller
public class MainFrame extends JFrame {
    private final SupplierRepository supplierRepository;
    private final ApplicationState appState;
    private final ObjectFactory<MainPanel> mainPanelFactory;
    private final LoginPanel loginPanel;

    private CardLayout cardLayout;
    private JPanel cardContainer;
    private MainPanel mainPanel;

    MainFrame(SupplierRepository supplierRepository,
              ApplicationState appState,
              ObjectFactory<MainPanel> mainPanelFactory,
              LoginPanel loginPanel) {
        this.supplierRepository = supplierRepository;
        this.appState = appState;
        this.mainPanelFactory = mainPanelFactory;
        this.loginPanel = loginPanel;
    }

    @PostConstruct
    private void init() {
        setTitle("NHTS Store");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Initialize UI
        FlatIntelliJLaf.setup();

        // Create card layout for switching between login and main screen
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        getContentPane().add(cardContainer);

        // Add login panel
        cardContainer.add(loginPanel, "login");

        // Show login initially
        cardLayout.show(cardContainer, "login");

        // Listen for authentication changes
        appState.authenticatedProperty().addListener((observable, oldValue, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                if (isAuthenticated) {
                    if (mainPanel == null) {
                        // Create a fresh MainPanel instance when needed
                        mainPanel = mainPanelFactory.getObject();
                        cardContainer.add(mainPanel, "main");
                    }
                    cardLayout.show(cardContainer, "main");
                } else {
                    // Remove and clear mainPanel on logout
                    if (mainPanel != null) {
                        cardContainer.remove(mainPanel);
                        mainPanel = null;
                    }
                    // Reset login fields before showing login panel
                    loginPanel.resetFields();
                    cardLayout.show(cardContainer, "login");
                }
                cardContainer.revalidate();
                cardContainer.repaint();
            });
        });

        setVisible(true);
    }
}
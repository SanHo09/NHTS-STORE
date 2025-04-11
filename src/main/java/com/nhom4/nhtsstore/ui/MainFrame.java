package com.nhom4.nhtsstore.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;

@Controller
public class MainFrame extends JFrame {
    private final ApplicationState appState;
    private final ObjectFactory<MainPanel> mainPanelFactory;
    private final LoginPanel loginPanel;

    private final MainPanel mainPanel;

    MainFrame(ApplicationState appState,
              ObjectFactory<MainPanel> mainPanelFactory,
              LoginPanel loginPanel, MainPanel mainPanel) {
        this.appState = appState;
        this.mainPanelFactory = mainPanelFactory;
        this.loginPanel = loginPanel;
        this.mainPanel = mainPanel;
    }

    @PostConstruct
    private void init() {
        setTitle("NHTS Store");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        CardLayout cardLayout = new CardLayout();

        JPanel cardContainer = new JPanel(cardLayout);
        cardContainer.add(loginPanel, "login");

        appState.authenticatedProperty().addListener((obs, old, isAuthenticated) -> {
            if (isAuthenticated) {
                cardContainer.add(mainPanel, "main");
                cardLayout.show(cardContainer, "main");
            } else {
                cardLayout.show(cardContainer, "login");

            }
        });
        cardLayout.show(cardContainer,"login");
        add(cardContainer, BorderLayout.CENTER);
        Platform.runLater(() -> {
            FlatIntelliJLaf.setup();
            setVisible(true);
        });
    }
}
package com.nhom4.nhtsstore.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.ui.layout.WindowLayout;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Controller;
import raven.modal.component.ModalContainer;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        add(JavaFxSwing.createJFXPanelWithController("/fxml/WindowLayout.fxml",appState.getApplicationContext(),
                        false,
                        (WindowLayout controller) -> {
                            controller.setMainFrame(this);
                        }
                )
            ,BorderLayout.NORTH
        );
        CardLayout cardLayout = new CardLayout();
        setUndecorated(true);
        JPanel cardContainer = new JPanel(cardLayout);
        cardContainer.add(loginPanel, "login");
        cardContainer.add(mainPanel, "main");
        appState.authenticatedProperty().addListener((obs, old, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                cardLayout.show(cardContainer, isAuthenticated ? "main" : "login");

            });
        });

        add(cardContainer, BorderLayout.CENTER);

    }

}
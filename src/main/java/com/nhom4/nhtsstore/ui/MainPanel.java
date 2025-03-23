package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.customComponent.BreadcrumbBar;
import com.nhom4.nhtsstore.ui.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.layout.ApplicationHeader;
import com.nhom4.nhtsstore.ui.layout.NavigationSidebar;
import com.nhom4.nhtsstore.ui.layout.ViewContainer;
import com.nhom4.nhtsstore.ui.login.LoginPanel;
import com.nhom4.nhtsstore.utils.PanelManager;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;

@Controller
public class MainPanel extends JPanel {
    private final ApplicationState applicationState;
    private final PanelManager panelManager;
    private final ApplicationContext applicationContext;
    private LoginPanel loginPanel;
    private final ViewContainer viewContainer;
    private final NavigationSidebar navigationSidebar;
    private final ApplicationHeader applicationHeader;

    private JFXPanel breadcrumbPanel;
    private CardLayout cardLayout;
    private JPanel cardContainer;

    public MainPanel(ApplicationState applicationState, PanelManager panelManager,
                     ApplicationContext applicationContext, LoginPanel loginPanel,
                     ViewContainer viewContainer, NavigationSidebar navigationSidebar,
                     ApplicationHeader applicationHeader) {
        this.applicationState = applicationState;
        this.panelManager = panelManager;
        this.applicationContext = applicationContext;
        this.loginPanel = loginPanel;
        this.viewContainer = viewContainer;
        this.navigationSidebar = navigationSidebar;
        this.applicationHeader = applicationHeader;

        setLayout(new BorderLayout());
        initializeComponents();
    }

    @PostConstruct
    private void initializeComponents() {
        // Create card layout container
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        add(cardContainer, BorderLayout.CENTER);

        // Add login panel card
        cardContainer.add(loginPanel, "login");

        // Create main application UI with header, sidebar, breadcrumb and content
        JPanel mainContentPanel = new JPanel(new BorderLayout());

        // Add header at the top
        mainContentPanel.add(applicationHeader, BorderLayout.NORTH);

        // Create center panel with sidebar and content
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Add sidebar on the left
        centerPanel.add(navigationSidebar, BorderLayout.WEST);

        // Create right panel with breadcrumb and view container
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Add breadcrumb at the top of right panel
        breadcrumbPanel = new JFXPanel();
        rightPanel.add(breadcrumbPanel, BorderLayout.NORTH);

        // Add view container in the center of right panel
        rightPanel.add(viewContainer, BorderLayout.CENTER);

        // Add right panel to center area
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        // Add center panel to main content
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);

        // Add main content panel to card container
        cardContainer.add(mainContentPanel, "main");

        // Show login initially
        cardLayout.show(cardContainer, "login");

        // Initialize JavaFX breadcrumb
        Platform.runLater(this::setupBreadcrumb);

        // Listen for authentication changes
        applicationState.authenticatedProperty().addListener((observable, oldValue, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                cardLayout.show(cardContainer, isAuthenticated ? "main" : "login");
                if (isAuthenticated) {
                    // Set default view when authenticated
                    if (applicationState.currentViewProperty().get() == null) {
                        panelManager.navigateTo(ViewName.DASHBOARD_VIEW,applicationState.getViewPanelByBean(DashBoardPanel.class));
                    }
                }
            });
        });
    }

    private void setupBreadcrumb() {
        BorderPane borderPane = new BorderPane();
        BreadcrumbBar breadcrumbBar = applicationContext.getBean(BreadcrumbBar.class);
        borderPane.setCenter(breadcrumbBar);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("/css/Global.css").toExternalForm());

        breadcrumbPanel.setScene(scene);
    }
}
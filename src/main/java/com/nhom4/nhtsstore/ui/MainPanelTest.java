package com.nhom4.nhtsstore.ui;


import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.utils.PanelManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
@Scope("prototype")
public class MainPanelTest extends JPanel {
    private final ApplicationState applicationState;
    private final PanelManager panelManager;
    private final ApplicationContext applicationContext;


//    private JFXPanel breadcrumbPanel;

    public MainPanelTest(ApplicationState applicationState, PanelManager panelManager,
                     ApplicationContext applicationContext) {
        this.applicationState = applicationState;
        this.panelManager = panelManager;
        this.applicationContext = applicationContext;
        setLayout(new BorderLayout());
        initializeComponents();
    }

    @PostConstruct
    private void initializeComponents() {
        // Set default view when panel is created
        SwingUtilities.invokeLater(() -> {
            panelManager.navigateTo(ViewName.DASHBOARD_VIEW,
                    applicationState.getViewPanelByBean(DashBoardPanel.class));
        });
    }

}
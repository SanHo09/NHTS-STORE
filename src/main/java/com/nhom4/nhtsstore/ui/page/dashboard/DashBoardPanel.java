package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.SwingThemeUtil;
import javafx.application.Platform;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import java.awt.*;

@Controller

public class DashBoardPanel extends JPanel implements RoutablePanel {
    private final NavigationService navigationService;
    private final IDashboardStatisticsService dashboardStatistics;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    private DashBoardFxView dashBoardFxView;
    private JPanel headerPanel;
    private JLabel titleLabel;

    public DashBoardPanel(
            NavigationService navigationService, 
            IDashboardStatisticsService dashboardStatistics,
            ThemeManager themeManager,
            LanguageManager languageManager) {
        this.navigationService = navigationService;
        this.dashboardStatistics = dashboardStatistics;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
        this.dashBoardFxView = new DashBoardFxView(dashboardStatistics, themeManager, languageManager);
        setLayout(new BorderLayout(5, 5));
        initComponents();
        setupLanguageListener();

        SwingThemeUtil.applyThemeAndListenForChanges(this, themeManager);
    }
    
    private void initComponents() {
        headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel(languageManager.getText("dashboard.title"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(dashBoardFxView, BorderLayout.CENTER);
    }
    
    private void setupLanguageListener() {
        // Add listener for language changes
        languageManager.addLanguageChangeListener(() -> {
            SwingUtilities.invokeLater(() -> {
                titleLabel.setText(languageManager.getText("dashboard.title"));
            });
        });
    }

    @Override
    public void onNavigate(RouteParams params) {

        Platform.runLater(dashBoardFxView::refreshData);
    }
}

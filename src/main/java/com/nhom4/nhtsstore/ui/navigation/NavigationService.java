package com.nhom4.nhtsstore.ui.navigation;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.SidebarManager;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.Stack;

@Service
public class NavigationService {
    private final PanelManager panelManager;
    private final ApplicationState applicationState;
    private final SidebarManager sidebarManager;

    @Getter
    private AppView currentView;
    @Getter
    private RouteParams currentParams = new RouteParams();



    public NavigationService(PanelManager panelManager,
                             ApplicationState applicationState,
                             SidebarManager sidebarManager) {
        this.panelManager = panelManager;
        this.applicationState = applicationState;
        this.sidebarManager = sidebarManager;
    }

    public void navigateTo(AppView view) {
        navigateTo(view, new RouteParams());
    }

    public void navigateTo(AppView view, RouteParams params) {


        JPanel panel = applicationState.getViewPanelByBean(view.getPanelClass());

        // Set the current route parameters
        this.currentView = view;
        this.currentParams = params;

        // If panel implements RoutablePanel, pass the parameters
        try {
            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Navigate to the panel
        panelManager.navigateTo(view, panel);

        // Update sidebar selection
        sidebarManager.selectMenuItem(view);
    }

    public void navigateTo(Class<? extends JPanel> panelClass, RouteParams params) {

        JPanel panel = applicationState.getViewPanelByBean(panelClass);

        // Set the current route parameters
        this.currentParams = params;

        // If panel implements RoutablePanel, pass the parameters
        try {
            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Navigate to the panel
        panelManager.navigateTo(null, panel);
    }


}
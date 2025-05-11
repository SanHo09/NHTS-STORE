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
        JPanel panel;
        try {
            panel=applicationState.getViewPanelByBean(view.getPanelClass());
            this.currentView = view;
            this.currentParams = params;
            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
            // Navigate to the panel
            panelManager.navigateTo(view, panel);

            // Update sidebar selection
            sidebarManager.selectMenuItem(view);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void navigateTo(Class<? extends JPanel> panelClass, RouteParams params) {
        try {
            JPanel panel = applicationState.getViewPanelByBean(panelClass);
            this.currentParams = params;
            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
            panelManager.navigateTo(null, panel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }


}
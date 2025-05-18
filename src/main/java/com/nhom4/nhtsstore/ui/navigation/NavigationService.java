package com.nhom4.nhtsstore.ui.navigation;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

@Service
public class NavigationService {
    private final PanelManager panelManager;
    private final ApplicationState applicationState;
    private final List<Consumer<AppView>> navigationListeners = new ArrayList<>();

    @Getter
    private AppView currentView;
    @Getter
    private AppView previousView;
    @Getter
    private RouteParams currentParams = new RouteParams();

    public NavigationService(PanelManager panelManager,
                             ApplicationState applicationState) {
        this.panelManager = panelManager;
        this.applicationState = applicationState;

    }

    /**
     * Add a listener that will be called when navigation occurs
     * @param listener Consumer that will receive the AppView being navigated to
     */
    public void addNavigationListener(Consumer<AppView> listener) {
        navigationListeners.add(listener);
    }

    public void navigateTo(AppView view) {
        navigateTo(view, new RouteParams());
    }

    public void navigateTo(AppView view, RouteParams params) {
        JPanel panel;
        try {
            panel=applicationState.getViewPanelByBean(view.getPanelClass());
            this.previousView=this.currentView;
            this.currentView = view;
            this.currentParams = params;
            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
            // Navigate to the panel
            panelManager.navigateTo(view, panel);

            for (Consumer<AppView> listener : navigationListeners) {
                listener.accept(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }

    }

    public void navigateTo(Class<? extends JPanel> panelClass, RouteParams params) {
        try {
            JPanel panel = applicationState.getViewPanelByBean(panelClass);
            this.previousView=this.currentView;
            this.currentParams = params;
            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
            panelManager.navigateTo(null, panel);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }

    }
    public void navigateBack() {
        if (previousView != null) {
            navigateTo(previousView, currentParams);
        } else {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(), "No previous view to navigate back to.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showErrorDialog(String message) {
        //reduce length of the message
        if (message.length() > 110) {
            message = message.substring(0, 100) + "...";
        }
        JOptionPane.showMessageDialog(panelManager.getContentContainer(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
package com.nhom4.nhtsstore.ui.navigation;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.SidebarManager;
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
    private final SidebarManager sidebarManager;
    private final List<Consumer<AppView>> navigationListeners = new ArrayList<>();

    private final Stack<NavigationHistoryEntry> backHistory = new Stack<>();
    private final Stack<NavigationHistoryEntry> forwardHistory = new Stack<>();

    @Getter
    private AppView currentView;
    @Getter
    private RouteParams currentParams = new RouteParams();

    private record NavigationHistoryEntry(AppView view, RouteParams params) {
            private NavigationHistoryEntry(AppView view, RouteParams params) {
                this.view = view;
                this.params = params.copy();
            }
        }

    public NavigationService(PanelManager panelManager,
                             ApplicationState applicationState,
                             SidebarManager sidebarManager) {
        this.panelManager = panelManager;
        this.applicationState = applicationState;
        this.sidebarManager = sidebarManager;
    }

    public void addNavigationListener(Consumer<AppView> listener) {
        navigationListeners.add(listener);
    }

    public void navigateTo(AppView view) {
        navigateTo(view, new RouteParams());
    }

    public void navigateTo(AppView view, RouteParams params) {
        try {
            JPanel panel = applicationState.getViewPanelByBean(view.getPanelClass());

            // Save current view to history before changing
            if (currentView != null) {
                backHistory.push(new NavigationHistoryEntry(currentView, currentParams));
            }

            // Clear forward history when navigating to a new view
            forwardHistory.clear();

            // Update current view and parameters
            currentView = view;
            currentParams = params.copy();

            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }

            // Navigate to the panel
            panelManager.navigateTo(view, panel);

            // Update sidebar selection
            sidebarManager.selectMenuItem(view);

            // Notify navigation listeners
            for (Consumer<AppView> listener : navigationListeners) {
                listener.accept(view);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(),
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void navigateTo(Class<? extends JPanel> panelClass, RouteParams params) {
        try {
            JPanel panel = applicationState.getViewPanelByBean(panelClass);

            // We don't store history for direct panel navigation
            // since it doesn't map to an AppView
            currentParams = params.copy();

            if (panel instanceof RoutablePanel) {
                ((RoutablePanel) panel).onNavigate(params);
            }
            panelManager.navigateTo(null, panel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(),
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean canNavigateBack() {
        return !backHistory.isEmpty();
    }

    public boolean canNavigateForward() {
        return !forwardHistory.isEmpty();
    }

    public void navigateBack() {
        if (!canNavigateBack()) {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(),
                    "No previous view to navigate back to.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        NavigationHistoryEntry previous = backHistory.pop();

        // Save current view to forward history
        forwardHistory.push(new NavigationHistoryEntry(currentView, currentParams));

        // Navigate to previous view
        AppView previousView = previous.view;
        RouteParams previousParams = previous.params;

        JPanel panel = applicationState.getViewPanelByBean(previousView.getPanelClass());
        currentView = previousView;
        currentParams = previousParams;

        if (panel instanceof RoutablePanel) {
            ((RoutablePanel) panel).onNavigate(previousParams);
        }

        panelManager.navigateTo(previousView, panel);
        sidebarManager.selectMenuItem(previousView);

        // Notify navigation listeners
        for (Consumer<AppView> listener : navigationListeners) {
            listener.accept(previousView);
        }
    }

    public void navigateForward() {
        if (!canNavigateForward()) {
            JOptionPane.showMessageDialog(panelManager.getContentContainer(),
                    "No forward view to navigate to.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        NavigationHistoryEntry next = forwardHistory.pop();

        // Save current view to back history
        backHistory.push(new NavigationHistoryEntry(currentView, currentParams));

        // Navigate to next view
        AppView nextView = next.view;
        RouteParams nextParams = next.params;

        JPanel panel = applicationState.getViewPanelByBean(nextView.getPanelClass());
        currentView = nextView;
        currentParams = nextParams;

        if (panel instanceof RoutablePanel) {
            ((RoutablePanel) panel).onNavigate(nextParams);
        }

        panelManager.navigateTo(nextView, panel);
        sidebarManager.selectMenuItem(nextView);

        // Notify navigation listeners
        for (Consumer<AppView> listener : navigationListeners) {
            listener.accept(nextView);
        }
    }

    public void clearHistory() {
        backHistory.clear();
        forwardHistory.clear();
    }

    public int getBackHistorySize() {
        return backHistory.size();
    }

    public int getForwardHistorySize() {
        return forwardHistory.size();
    }
}
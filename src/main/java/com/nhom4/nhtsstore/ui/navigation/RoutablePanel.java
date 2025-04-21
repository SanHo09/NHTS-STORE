package com.nhom4.nhtsstore.ui.navigation;

/**
 * Interface for panels that can receive route parameters
 */
public interface RoutablePanel {
    /**
     * Called when navigating to this panel with parameters
     */
    void onNavigate(RouteParams params);
}
package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.ViewName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.springframework.stereotype.Component;

import javax.swing.JPanel;
import java.util.*;

@Component
public class PanelManager {
    private final LinkedHashMap<ViewName, JPanel> panels = new LinkedHashMap<>();
    private final ListProperty<ViewName> navigationHistory = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty currentHistoryPosition = new SimpleIntegerProperty(-1);
    private final ApplicationState state;

    public PanelManager(ApplicationState state) {
        this.state = state;
    }

    public ListProperty<ViewName> navigationHistoryProperty() {
        return navigationHistory;
    }

    public IntegerProperty currentPositionProperty() {
        return currentHistoryPosition;
    }

    public List<ViewName> getNavigationHistory() {
        return navigationHistory;
    }

    public int getCurrentHistoryPosition() {
        return currentHistoryPosition.get();
    }

    public void setCurrentHistoryPosition(int position) {
        currentHistoryPosition.set(position);
    }

    public void navigateTo(ViewName viewName, JPanel panel) {
        panels.put(viewName, panel);

        int currentPos = currentHistoryPosition.get();
        if (currentPos < navigationHistory.size() - 1) {
            navigationHistory.remove(currentPos + 1, navigationHistory.size());
        }
        navigationHistory.add(viewName);
        currentHistoryPosition.set(navigationHistory.size() - 1);

        state.currentViewProperty().set(viewName);
    }

    public boolean canGoBack() {
        return currentHistoryPosition.get() > 0;
    }

    public boolean canGoForward() {
        return currentHistoryPosition.get() < navigationHistory.size() - 1;
    }

    public void goBack() {
        if (canGoBack()) {
            int newPos = currentHistoryPosition.get() - 1;
            currentHistoryPosition.set(newPos);
            updateCurrentView(newPos);
        }
    }

    public void goForward() {
        if (canGoForward()) {
            int newPos = currentHistoryPosition.get() + 1;
            currentHistoryPosition.set(newPos);
            updateCurrentView(newPos);
        }
    }

    public void navigateToBreadcrumb(int position) {
        if (position >= 0 && position < navigationHistory.size()) {
            currentHistoryPosition.set(position);
            updateCurrentView(position);
        }
    }

    private void updateCurrentView(int position) {
        ViewName viewName = navigationHistory.get(position);
        state.currentViewProperty().set(viewName);
    }

    public JPanel getPanel(ViewName viewName) {
        return panels.get(viewName);
    }

    public JPanel getCurrentPanel() {
        ViewName currentView = state.currentViewProperty().get();
        for (ViewName viewName : ViewName.values()) {
            if (viewName.getValue().equals(currentView)) {
                return panels.get(viewName);
            }
        }
        return null;
    }
}
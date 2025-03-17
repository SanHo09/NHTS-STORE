package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.ui.ApplicationState;
import org.springframework.stereotype.Component;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;

@Component
public class PanelManager {
    private final Map<String, JPanel> panels = new HashMap<>();
    private final ApplicationState state;

    public PanelManager(ApplicationState state) {
        this.state = state;
    }

    public void navigateTo(String viewName, JPanel panel) {
        panels.put(viewName, panel);
        state.currentViewProperty().set(viewName);
    }

    public JPanel getPanel(String viewName) {
        return panels.get(viewName);
    }

    public JPanel getCurrentPanel() {
        return panels.get(state.currentViewProperty().get());
    }
}
package com.nhom4.nhtsstore.utils;

import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class PanelManager {
    private final Map<String, JPanel> panelCache = new HashMap<>();
    @Setter
    private JPanel mainContainer;

    public void navigateTo(String route, JPanel panel) {
        if (mainContainer == null) {
            throw new IllegalStateException("Main container not set");
        }

        mainContainer.removeAll();
        mainContainer.add(panel);
        mainContainer.revalidate();
        mainContainer.repaint();

        panelCache.put(route, panel);
    }

    public JPanel getPanel(String route) {
        return panelCache.get(route);
    }

    public void clearCache() {
        panelCache.clear();
    }
}
// Instead of RoutePanel.java
package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.ViewName;
import com.nhom4.nhtsstore.utils.PanelManager;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class ViewContainer extends JPanel {
    private final PanelManager panelManager;
    private final ApplicationState applicationState;

    public ViewContainer(PanelManager panelManager, ApplicationState applicationState) {
        this.panelManager = panelManager;
        this.applicationState = applicationState;

        setLayout(new BorderLayout());

        applicationState.currentViewProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> updateContent(newValue.getValue()));
        });
    }

    private void updateContent(String viewValue) {
        removeAll();

        for (ViewName viewName : ViewName.values()) {
            if (viewName.getValue().equals(viewValue)) {
                JPanel panel = panelManager.getPanel(viewName);
                if (panel != null) {
                    add(panel, BorderLayout.CENTER);
                }
                break;
            }
        }

        revalidate();
        repaint();
    }
}
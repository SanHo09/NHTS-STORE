package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.layout.PagePanel;
import org.springframework.stereotype.Component;

import javax.swing.JPanel;
import java.util.*;

@Component
public class PanelManager {
    private final LinkedHashMap<AppView, JPanel> panels = new LinkedHashMap<>();
    private final ApplicationState state;
    private final PagePanel contentContainer;
    public PanelManager(ApplicationState state, PagePanel contentContainer) {
        this.state = state;
        this.contentContainer = contentContainer;
    }



    public void navigateTo(AppView appView, JPanel panel) {
        if(panel == null) {
            return;
        }
        if (appView == null) {
            contentContainer.showPanel(panel);
            return;
        }
        if (panels.containsKey(appView)) {
            contentContainer.showPanel(panels.get(appView));
            return;
        }

        panels.put(appView, panel);
        contentContainer.showPanel(panel);
    }


    public JPanel getPanel(AppView appView) {
        return panels.get(appView);
    }


}
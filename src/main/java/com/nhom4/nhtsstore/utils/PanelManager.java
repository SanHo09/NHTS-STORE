package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.AppView;
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
        panels.put(appView, panel);

//        state.currentViewProperty().set(viewName);
        contentContainer.showPanel(panel);
    }


    public JPanel getPanel(AppView appView) {
        return panels.get(appView);
    }


}
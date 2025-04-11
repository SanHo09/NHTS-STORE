package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.ViewName;
import com.nhom4.nhtsstore.ui.layout.PagePanel;
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
    private final ApplicationState state;
    private final PagePanel contentContainer;
    public PanelManager(ApplicationState state, PagePanel contentContainer) {
        this.state = state;
        this.contentContainer = contentContainer;
    }



    public void navigateTo(ViewName viewName, JPanel panel) {
        panels.put(viewName, panel);

        state.currentViewProperty().set(viewName);
        contentContainer.showPanel(panel);
    }


    public JPanel getPanel(ViewName viewName) {
        return panels.get(viewName);
    }


}
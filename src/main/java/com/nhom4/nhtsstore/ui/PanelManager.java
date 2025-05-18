package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.layout.PagePanel;
import lombok.Getter;
import org.springframework.stereotype.Component;
import javax.swing.JPanel;

@Component
public class PanelManager {
    private final ApplicationState state;
    @Getter
    private final PagePanel contentContainer;
    public PanelManager(ApplicationState state, PagePanel contentContainer) {
        this.state = state;
        this.contentContainer = contentContainer;
    }

    public void navigateTo(AppView appView, JPanel panel) {
        if (panel == null) {
            return;
        }
        contentContainer.showPanel(appView, panel);
    }



}

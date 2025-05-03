package com.nhom4.nhtsstore.ui.shared.components;

import javax.swing.*;
import java.awt.*;

public class GlobalLoadingManager {

    private static GlobalLoadingManager instance;
    private JLayeredPane container;
    private GlobalLoadingGlassPane glassPane;

    private GlobalLoadingManager() {
    }

    public static synchronized GlobalLoadingManager getInstance() {
        if (instance == null) {
            instance = new GlobalLoadingManager();
        }
        return instance;
    }

    public void init(JLayeredPane container) {
        this.container = container;
        this.glassPane = new GlobalLoadingGlassPane();
        this.glassPane.setVisible(false);
        container.add(glassPane, JLayeredPane.POPUP_LAYER);
        updateGlassPaneBounds();
    }

    private void updateGlassPaneBounds() {
        if (glassPane != null && container != null) {
            glassPane.setBounds(0, 0, container.getWidth(), container.getHeight());
            container.revalidate();
            container.repaint();
        }
    }

    public void showSpinner() {
        if (glassPane != null) {
            glassPane.setVisible(true);
            glassPane.repaint();
        }
    }

    public void hideSpinner() {
        if (glassPane != null) {
            glassPane.setVisible(false);
            glassPane.repaint();
        }
    }
}

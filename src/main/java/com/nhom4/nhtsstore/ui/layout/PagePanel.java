package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Component
public class PagePanel extends JPanel {
    private final JTabbedPane tabbedPane;
    private final Map<String, JPanel> panelMap = new HashMap<>();
    private final Map<JPanel, AppView> panelToAppViewMap = new HashMap<>();
    private final Map<Integer, AppView> tabIndexToAppViewMap = new HashMap<>();
    private final ApplicationState applicationState;

    public PagePanel(ApplicationState applicationState) {
        this.applicationState = applicationState;
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0) {
                AppView appView = tabIndexToAppViewMap.get(selectedIndex);
                if (appView == null) {
                    JPanel selectedPanel = (JPanel) tabbedPane.getComponentAt(selectedIndex);
                    appView = panelToAppViewMap.get(selectedPanel);
                }
                if (appView != null) {
                    applicationState.getCurrentView().set(appView);
                }
            }
        });
        applicationState.getAuthenticated().addListener((obs, oldValue, isAuthenticated) -> {
            if (!isAuthenticated) {
                panelToAppViewMap.clear();
                tabbedPane.removeAll();
                tabIndexToAppViewMap.clear();
                panelMap.clear();
                applicationState.getCurrentView().set(null);

            }
        });
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void showPanel(JPanel panel) {
        showPanel(null, panel);
    }

    public void showPanel(AppView appView, JPanel panel) {
        String panelName = panel.getClass().getSimpleName();
        String tabTitle = appView != null ? appView.getName() : panelName;

        if (appView != null) {
            // check if this app view already exists in any tab
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                AppView existingView = tabIndexToAppViewMap.get(i);
                if (existingView != null && existingView.equals(appView)) {
                    // Found existing tab with this app view
                    tabbedPane.setSelectedIndex(i);

                    // Update the component in the tab if it's different
                    if (tabbedPane.getComponentAt(i) != panel) {
                        tabbedPane.setComponentAt(i, panel);
                        panelMap.put(panelName, panel);
                        panelToAppViewMap.put(panel, appView);
                    }

                    return;
                }
            }

            // If not found by app view, check by panel name
            if (panelMap.containsKey(panelName)) {
                int index = tabbedPane.indexOfComponent(panelMap.get(panelName));
                if (index >= 0) {
                    tabbedPane.setSelectedIndex(index);
                    return;
                }
            }

            // Create a new tab if not found
            tabbedPane.addTab(tabTitle, panel);
            panelMap.put(panelName, panel);
            panelToAppViewMap.put(panel, appView);

            int index = tabbedPane.indexOfComponent(panel);
            tabIndexToAppViewMap.put(index, appView);
            tabbedPane.setTabComponentAt(index, new TabComponent(tabTitle, index));
            tabbedPane.setSelectedComponent(panel);
        } else {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0) {
                Component currentComponent = tabbedPane.getComponentAt(selectedIndex);
                AppView parentAppView = currentComponent instanceof JPanel ?
                        panelToAppViewMap.get(currentComponent) : null;

                tabbedPane.setComponentAt(selectedIndex, panel);
                panelMap.put(panelName, panel);

                if (parentAppView != null) {
                    panelToAppViewMap.put(panel, parentAppView);
                    tabIndexToAppViewMap.put(selectedIndex, parentAppView);
                }
            } else {
                tabbedPane.addTab(tabTitle, panel);
                panelMap.put(panelName, panel);
                int index = tabbedPane.indexOfComponent(panel);
                tabbedPane.setTabComponentAt(index, new TabComponent(tabTitle, index));
                tabbedPane.setSelectedComponent(panel);
            }
        }

        revalidate();
        repaint();
    }

    public void closeTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            JPanel panel = (JPanel) tabbedPane.getComponentAt(index);

            // Remove from maps
            panelMap.values().remove(panel);
            panelToAppViewMap.remove(panel);
            tabIndexToAppViewMap.remove(index);
            tabbedPane.removeTabAt(index);

            // Update tab indices
            Map<Integer, AppView> updatedMap = new HashMap<>();
            tabIndexToAppViewMap.forEach((tabIndex, view) -> {
                if (tabIndex > index) {
                    updatedMap.put(tabIndex - 1, view);
                } else if (tabIndex < index) {
                    updatedMap.put(tabIndex, view);
                }
            });
            tabIndexToAppViewMap.clear();
            tabIndexToAppViewMap.putAll(updatedMap);
        }
    }

    public void closeCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            closeTab(selectedIndex);
        }
    }

    public int getTabCount() {
        return tabbedPane.getTabCount();
    }

    private class TabComponent extends JPanel {
        public TabComponent(final String title, final int index) {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);

            JLabel label = new JLabel(title);
            label.setOpaque(false);

            JButton closeButton = new JButton("x");
            closeButton.setPreferredSize(new Dimension(26, 20));
            closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            closeButton.setToolTipText("Close this tab");
            closeButton.setFocusable(false);
            closeButton.addActionListener(e -> {
                int i = tabbedPane.indexOfTabComponent(TabComponent.this);
                if (i != -1) {
                    closeTab(i);
                }
            });

            add(label);
            add(closeButton);
        }
    }
}
package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.EventMenuSelected;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.ListMenu;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.Model_Menu;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.SidebarManager;
import com.nhom4.nhtsstore.utils.UIUtils;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;

@org.springframework.stereotype.Component
public class Menu extends javax.swing.JPanel implements LanguageManager.LanguageChangeListener {
    private final SidebarManager sidebarManager;
    private final ApplicationState applicationState;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    private EventMenuSelected event;
    private JToggleButton themeToggleButton;
    
    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
        listMenu1.addEventMenuSelected(event);
    }
    
    public Menu(SidebarManager sidebarManager, ApplicationState applicationState, 
                ThemeManager themeManager, LanguageManager languageManager) {
        this.sidebarManager = sidebarManager;
        this.applicationState = applicationState;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
        
        initComponents();
        setOpaque(false);
        listMenu1.setOpaque(false);

        // Register for language changes
        languageManager.addLanguageChangeListener(this);
        
        // Initialize menu items based on user role
        refreshMenuItems();

        // Set the ListMenu in SidebarManager
        sidebarManager.setListMenu(listMenu1);
        sidebarManager.initializeMenuMap();
    }

    public void refreshMenuItems() {
        // Clear existing menu items
        listMenu1.clearMenuItems();
        sidebarManager.clearMenuMap();

        // Rebuild menu items based on current user role
        int index = 0;
        for (AppView parent : AppView.values()) {
            if (parent == AppView.LOGIN) {
                continue;
            }

            // Check if user has permission to see this menu item
            if (!hasMenuPermission(parent)) {
                continue; // Skip this menu item
            }

            // Add main menu items
            if (parent.getParent() == null) {
                String localizedName = getLocalizedMenuName(parent);
                listMenu1.addItem(new Model_Menu(parent.getIcon(), localizedName, Model_Menu.MenuType.MENU));
                sidebarManager.registerMenuItem(parent, index++);

                // Check for submenu items
                for (AppView children : AppView.values()) {
                    if (children.getParent() == parent && hasMenuPermission(children)) {
                        String localizedChildName = getLocalizedMenuName(children);
                        listMenu1.addItem(new Model_Menu(children.getIcon(), localizedChildName, Model_Menu.MenuType.MENU));
                        sidebarManager.registerMenuItem(children, index++);
                    }
                }
            }
        }

        // Refresh UI
        revalidate();
        repaint();
    }
    
    /**
     * Get the localized name for a menu item
     */
    private String getLocalizedMenuName(AppView view) {
        String key = "nav." + view.name().toLowerCase();
        return languageManager.getText(key);
    }

    private boolean hasMenuPermission(AppView view) {
        // If user isn't logged in, only show LOGIN
        if (!applicationState.isAuthenticated()) {
            return view == AppView.LOGIN;
        }

        UserSessionVm currentUser = applicationState.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Check if view requires SUPER_ADMIN role
        if (view.requiresSuperAdmin()) {
            return currentUser.getRole() != null &&
                    "SUPER_ADMIN".equals(currentUser.getRole());
        }

        // All other views are accessible to authenticated users
        return true;
    }

    @Override
    public void onLanguageChanged() {
        // Refresh menu items with new language
        refreshMenuItems();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listMenu1 = new com.nhom4.nhtsstore.ui.shared.components.sidebar.ListMenu<>();

        setPreferredSize(new java.awt.Dimension(215, 343));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, Color.decode("#1CB5E0"), 0, getHeight(), Color.decode("#000046"));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(getWidth() - 20, 0, getWidth(), getHeight());
        super.paintChildren(g);
    }

    public ListMenu<String> getListMenu1() {
        return listMenu1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.nhom4.nhtsstore.ui.shared.components.sidebar.ListMenu<String> listMenu1;
    // End of variables declaration//GEN-END:variables
}
package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.EventMenuSelected;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.ListMenu;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.Model_Menu;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.SidebarManager;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;

@org.springframework.stereotype.Component
public class Menu extends javax.swing.JPanel {
    private final SidebarManager sidebarManager;

    private EventMenuSelected event;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
        listMenu1.addEventMenuSelected(event);
    }
    // Add SidebarManager to constructor
    public Menu(SidebarManager sidebarManager) {
        this.sidebarManager = sidebarManager;
        initComponents();
        setOpaque(false);
        listMenu1.setOpaque(false);

        // Initialize menu items
        initMenuItems();

        // Set the ListMenu in SidebarManager
        sidebarManager.setListMenu(listMenu1);
        sidebarManager.initializeMenuMap();
    }

    private void initMenuItems() {
        int index = 0;
        for (AppView parent : AppView.values()) {
            if (parent == AppView.LOGIN) {
                continue;
            }

            // Only add main menu items (those without parents)
            if (parent.getParent() == null) {
                listMenu1.addItem(new Model_Menu(parent.getIcon(), parent.getName(), Model_Menu.MenuType.MENU));
                sidebarManager.registerMenuItem(parent, index++);

                // Check for submenu items
                for (AppView children : AppView.values()) {
                    if (children.getParent() == parent) {
                        // Add submenu items
                        listMenu1.addItem(new Model_Menu(children.getIcon(), children.getName(), Model_Menu.MenuType.MENU));
                        sidebarManager.registerMenuItem(children, index++);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMoving = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        listMenu1 = new com.nhom4.nhtsstore.ui.shared.components.sidebar.ListMenu<>();

        setPreferredSize(new java.awt.Dimension(215, 343));

        panelMoving.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Grocery Application");

        javax.swing.GroupLayout panelMovingLayout = new javax.swing.GroupLayout(panelMoving);
        panelMoving.setLayout(panelMovingLayout);
        panelMovingLayout.setHorizontalGroup(
                panelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMovingLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelMovingLayout.setVerticalGroup(
                panelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelMovingLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel1)
                                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panelMoving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(panelMoving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
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

    private int x;
    private int y;

    public void initMoving(JPanel panel) {
        panelMoving.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                x = me.getX();
                y = me.getY();
            }
        });
        panelMoving.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                panel.setLocation(me.getXOnScreen() - x, me.getYOnScreen() - y);
            }
        });
    }

    public ListMenu<String> getListMenu1() {
        return listMenu1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private com.nhom4.nhtsstore.ui.shared.components.sidebar.ListMenu<String> listMenu1;
    private javax.swing.JPanel panelMoving;
    // End of variables declaration//GEN-END:variables
}
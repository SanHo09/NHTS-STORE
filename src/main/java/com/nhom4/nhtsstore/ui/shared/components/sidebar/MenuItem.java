package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import com.nhom4.nhtsstore.utils.UIUtils;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import javax.swing.JLabel;

public class MenuItem extends javax.swing.JPanel {
    private boolean selected;
    private boolean over;
    private boolean isSubmenu;
    private boolean hasSubmenus;
    private boolean expanded;
    private final Model_Menu menuData;
    private JLabel expandIcon;
    private MenuItemListener listener;

    public interface MenuItemListener {
        void onAccordionToggled(String menuId, boolean expanded);
    }

    public void setMenuItemListener(MenuItemListener listener) {
        this.listener = listener;
    }

    public MenuItem(Model_Menu data) {
        initComponents();
        setOpaque(false);
        // Set layout to null to have absolute control
        setLayout(null);
        
        this.menuData = data;
        this.isSubmenu = data.isSubmenu();
        this.hasSubmenus = data.hasSubmenus();
        this.expanded = data.isExpanded();
        
        // Position labels manually with null layout
        lblIcon.setBounds(isSubmenu ? 40 : 20, 10, 24, 24);
        lblName.setBounds(isSubmenu ? 80 : 60, 10, 120, 24);
        
        add(lblIcon);
        add(lblName);
        
        if (data.getType() == Model_Menu.MenuType.MENU || data.getType() == Model_Menu.MenuType.SUBMENU) {
            lblIcon.setIcon(UIUtils.toIcon(data.getIcon(), null));
            lblName.setText(data.getName());
            
            if (data.isSubmenu()) {
                // Style for submenu items
                lblName.setFont(new Font("sansserif", Font.PLAIN, 12));
                lblName.setForeground(new Color(200, 200, 200));
            }
            
            // Add expand/collapse icon for parent menus with submenus
            if (hasSubmenus) {
                expandIcon = new JLabel();
                expandIcon.setForeground(new Color(255, 255, 255));
                expandIcon.setText(expanded ? "▼" : "►");
                expandIcon.setFont(new Font("sansserif", Font.BOLD, 10));
                expandIcon.setBounds(getWidth() - 30, 10, 20, 24);
                add(expandIcon);
                
                // Add mouse listener for expand/collapse action
                this.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Check if click is in the expand/collapse area
                        if (hasSubmenus && e.getX() >= getWidth() - 40) {
                            expanded = !expanded;
                            expandIcon.setText(expanded ? "▼" : "►");
                            
                            if (listener != null) {
                                System.out.println("Toggle accordion: " + menuData.getMenuId() + " to " + expanded);
                                listener.onAccordionToggled(menuData.getMenuId(), expanded);
                            }
                            
                            repaint();
                        }
                    }
                });
            }
        } else if (data.getType() == Model_Menu.MenuType.TITLE) {
            lblIcon.setText(data.getName());
            lblIcon.setFont(new Font("sansserif", 1, 12));
            lblName.setVisible(false);
        } else {
            lblName.setText(" ");
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    
    public void setOver(boolean over) {
        this.over = over;
        repaint();
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expandIcon != null) {
            expandIcon.setText(expanded ? "▼" : "►");
        }
        repaint();
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Reposition the expand icon when bounds change
        if (expandIcon != null) {
            expandIcon.setBounds(width - 30, 10, 20, 24);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblIcon = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(300, 45));

        lblIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblIcon.setForeground(new java.awt.Color(255, 255, 255));

        lblName.setBackground(new java.awt.Color(255, 255, 255));
        lblName.setForeground(new java.awt.Color(255, 255, 255));
        lblName.setText("Menu Name");
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        if (selected || over) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected) {
                g2.setColor(new Color(255, 255, 255, 80));
            } else {
                g2.setColor(new Color(255, 255, 255, 20));
            }
            
            // Use a different style for submenus
            if (isSubmenu) {
                g2.fillRoundRect(20, 0, getWidth() - 40, getHeight(), 5, 5);
            } else {
                g2.fillRoundRect(0, 0, getWidth() - 20, getHeight(), 5, 5);
            }
        }
        
        // Draw submenu indicator
        if (isSubmenu) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200, 80));
            g2.fillRect(30, getHeight()/2, 5, 1);
        }
        
        // Debug information - draw colored rectangles to visualize layout
        if (hasSubmenus) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(255, 0, 0, 30));
            g2.fillRect(getWidth() - 40, 0, 40, getHeight());
        }
        
        super.paintComponent(g);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblName;
    // End of variables declaration//GEN-END:variables
}

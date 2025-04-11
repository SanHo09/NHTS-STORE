package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.ViewName;
import com.nhom4.nhtsstore.ui.shared.components.ProfileMenuItem; // Fix import
import com.nhom4.nhtsstore.ui.shared.components.RoundedPanel; // Add import for new class
import com.nhom4.nhtsstore.utils.PanelManager;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
@Component
public class Header extends javax.swing.JPanel {

    private final ApplicationState applicationState;
    private final PanelManager panelManager;
    private JPanel profileMenuPanel;
    private boolean isProfileMenuExpanded = false;
    private RoundedPanel avatarPanel;

    public Header(ApplicationState applicationState, PanelManager panelManager) {
        this.applicationState = applicationState;
        this.panelManager = panelManager;
        initComponents();
        setupProfileMenu();
        setOpaque(false);

        // Subscribe to user changes
        applicationState.currentUserProperty().addListener((obs, oldValue, newValue) -> {
            updateUserDisplay(newValue);
        });
    }

    private void setupProfileMenu() {
        // Create user avatar panel
        avatarPanel = new RoundedPanel(25);
        avatarPanel.setBackground(new Color(0, 123, 255));
        avatarPanel.setPreferredSize(new Dimension(40, 40));
        avatarPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add user initial or icon to avatar
        JLabel avatarLabel = new JLabel("U");
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 16));
        avatarPanel.setLayout(new BorderLayout());
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // Create dropdown menu
        profileMenuPanel = new JPanel();
        profileMenuPanel.setLayout(new BoxLayout(profileMenuPanel, BoxLayout.Y_AXIS));
        profileMenuPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        profileMenuPanel.setBackground(Color.WHITE);
        profileMenuPanel.setVisible(false);

        // Add menu items
        ProfileMenuItem profileItem = new ProfileMenuItem("Profile", "/icons/user.png");
        ProfileMenuItem logoutItem = new ProfileMenuItem("Logout", "/icons/logout.png");

        profileMenuPanel.add(profileItem);
        profileMenuPanel.add(logoutItem);

        // Add click listeners
        profileItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navigate to profile view
                toggleProfileMenu();
                // Implement profile navigation if available
            }
        });

        logoutItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                applicationState.logout();
                toggleProfileMenu();
                panelManager.navigateTo(ViewName.LOGIN_VIEW,
                        applicationState.getViewPanelByBean(ViewName.LOGIN_VIEW.getPanelClass()));
            }
        });

        // Toggle menu on avatar click
        avatarPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleProfileMenu();
            }
        });

        // Add components to the header
        add(avatarPanel);
        add(profileMenuPanel);

        // Position the profile menu below the avatar
        profileMenuPanel.setBounds(getWidth() - 150, 50, 120, 80);
    }

    private void toggleProfileMenu() {
        isProfileMenuExpanded = !isProfileMenuExpanded;
        profileMenuPanel.setVisible(isProfileMenuExpanded);
        repaint();
    }

    private void updateUserDisplay(UserSessionVm user) {
        if (user != null) {
            // Get first letter of username for avatar
            String initial = user.getUsername().substring(0, 1).toUpperCase();
            ((JLabel)avatarPanel.getComponent(0)).setText(initial);
        } else {
            ((JLabel)avatarPanel.getComponent(0)).setText("U");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(255, 255, 255));
        setLayout(null);  // Changed to absolute layout

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menu.png"))); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(jLabel2);
        jLabel2.setBounds(10, 5, 35, 35);

        setPreferredSize(new Dimension(getWidth(), 50));
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void doLayout() {
        super.doLayout();
        if (avatarPanel != null) {
            avatarPanel.setBounds(getWidth() - 50, 5, 40, 40);
        }
        if (profileMenuPanel != null) {
            profileMenuPanel.setBounds(getWidth() - 150, 50, 120, 80);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(0, 0, 25, getHeight());
        g2.fillRect(getWidth() - 25, getHeight() - 25, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
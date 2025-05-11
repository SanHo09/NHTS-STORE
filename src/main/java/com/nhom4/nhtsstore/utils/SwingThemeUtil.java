package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.enums.Theme;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Utility class for handling Swing component theming
 */
public class SwingThemeUtil {

    // Light theme colors
    public static final Color LIGHT_BACKGROUND = new Color(250, 250, 250);
    public static final Color LIGHT_PANEL_BACKGROUND = new Color(240, 240, 240);
    public static final Color LIGHT_FOREGROUND = Color.BLACK;
    public static final Color LIGHT_BORDER = new Color(200, 200, 200);
    
    // Dark theme colors
    public static final Color DARK_BACKGROUND = new Color(40, 40, 40);
    public static final Color DARK_PANEL_BACKGROUND = new Color(50, 50, 50);
    public static final Color DARK_FOREGROUND = Color.WHITE;
    public static final Color DARK_BORDER = new Color(70, 70, 70);
    
    /**
     * Applies theme to a JPanel
     * @param panel The panel to apply theme to
     * @param theme Current theme
     */
    public static void applyTheme(JPanel panel, Theme theme) {
        if (theme == Theme.DARK) {
            panel.setBackground(DARK_PANEL_BACKGROUND);
//            panel.setBorder(createBorder(theme));
        } else {
            panel.setBackground(LIGHT_PANEL_BACKGROUND);
//            panel.setBorder(createBorder(theme));
        }
    }
    
    /**
     * Applies theme to a JLabel
     * @param label The label to apply theme to
     * @param theme Current theme
     */
    public static void applyTheme(JLabel label, Theme theme) {
        if (theme == Theme.DARK) {
            label.setForeground(DARK_FOREGROUND);
        } else {
            label.setForeground(LIGHT_FOREGROUND);
        }
    }
    
    /**
     * Sets up a theme listener for a Swing component
     * @param component The component to set up theme listener for
     * @param themeManager Theme manager to listen to
     */
    public static void setupThemeListener(JComponent component, ThemeManager themeManager) {
        applyComponentTheme(component, themeManager.getCurrentTheme());
        
        themeManager.currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> applyComponentTheme(component, newValue));
        });
    }
    
    /**
     * Simple method to apply theme to any component and register for theme changes
     * This is a one-liner that can be called from any component, even without prototype scope
     * 
     * @param component The component to apply theme to
     * @param themeManager The theme manager
     */
    public static void applyThemeAndListenForChanges(JComponent component, ThemeManager themeManager) {
        setupThemeListener(component, themeManager);
    }
    
    /**
     * Applies theme to any JComponent based on its type
     */
    public static void applyComponentTheme(JComponent component, Theme theme) {
        if (component instanceof JPanel) {
            applyTheme((JPanel) component, theme);
        } else if (component instanceof JLabel) {
            applyTheme((JLabel) component, theme);
        } else {
            // Default theming
            if (theme == Theme.DARK) {
                component.setBackground(DARK_BACKGROUND);
                component.setForeground(DARK_FOREGROUND);
            } else {
                component.setBackground(LIGHT_BACKGROUND);
                component.setForeground(LIGHT_FOREGROUND);
            }
        }

        // Process child components
        for (Component child : component.getComponents()) {
            if (child instanceof JComponent) {
                applyComponentTheme((JComponent) child, theme);
            }
        }
        
        component.revalidate();
        component.repaint();
    }
    
    /**
     * Creates a border appropriate for the current theme
     */
//    public static Border createBorder(Theme theme) {
//        Color borderColor = theme == Theme.DARK ? DARK_BORDER : LIGHT_BORDER;
//        Border lineBorder = new LineBorder(borderColor, 1);
//        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
//        return new CompoundBorder(lineBorder, emptyBorder);
//    }
//
    /**
     * Creates a card panel border for the current theme
     */
    public static Border createCardBorder(Theme theme) {
        Color borderColor = theme == Theme.DARK ? DARK_BORDER : LIGHT_BORDER;
        Border lineBorder = new LineBorder(borderColor, 1, true);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
        return new CompoundBorder(lineBorder, emptyBorder);
    }
} 
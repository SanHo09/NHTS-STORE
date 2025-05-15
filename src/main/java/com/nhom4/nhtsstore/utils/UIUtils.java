package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.NhtsStoreApplication;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javafx.scene.image.Image;
import java.io.InputStream;

/**
 *
 * @author NamDang
 */
public class UIUtils {
    public static void applySelectAllOnFocus(Object... components) {
        for (Object component : components) {
            if (component instanceof JTextComponent textComponent) {
                textComponent.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        SwingUtilities.invokeLater(textComponent::selectAll);
                    }
                });
            } else if (component instanceof JSpinner spinner) {
                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
                    JTextField textField = defaultEditor.getTextField();
                    textField.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            SwingUtilities.invokeLater(textField::selectAll);
                        }
                    });
                }
            } else {
                System.err.println("Unsupported component: " + component.getClass().getName());
            }
        }
    }
    public static String formatCurrency(double value) {
        if (value >= 1_000_000) {
            return String.format("$%.2fM", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("$%.2fK", value / 1_000);
        } else {
            return String.format("$%.2f", value);
        }
    }
    public static Icon toIcon(String icon, Color color) {
        String path = "/icons/" + icon ;
        java.net.URL location = NhtsStoreApplication.class.getResource(path);
        if (location == null) {
            return new ImageIcon();
        }
        if (icon.endsWith(".svg")) {
            return IconUtil.createSwingIconFromSvg(path,24,24, color1 -> color);
        }
        return new ImageIcon(location);
    }
    
    /**
     * Load a JavaFX Image from a resource path
     * @param iconName Name of the icon file (will be loaded from /icons/ directory)
     * @return JavaFX Image object, or null if the image couldn't be loaded
     */
    public static Image loadJavaFXImage(String iconName) {
        try {
            String path = "/icons/" + iconName;
            InputStream is = NhtsStoreApplication.class.getResourceAsStream(path);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            System.err.println("Error loading JavaFX image: " + e.getMessage());
        }
        return null;
    }
}    
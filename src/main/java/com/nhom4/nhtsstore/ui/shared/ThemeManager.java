package com.nhom4.nhtsstore.ui.shared;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.nhom4.nhtsstore.configuration.setting.SettingsConfig;
import com.nhom4.nhtsstore.enums.Theme;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;


@Component
public class ThemeManager {

    private final SettingsConfig settingsConfig;
    private final ObjectProperty<Theme> currentTheme = new SimpleObjectProperty<>();

    public ThemeManager(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
        setTheme(settingsConfig.getTheme());
    }

    public Theme getCurrentTheme() {
        currentTheme.set(settingsConfig.getTheme());
        return currentTheme.getValue();
    }

    public ObjectProperty<Theme> currentThemeProperty() {
        return currentTheme;
    }


    public void setTheme(Theme theme) {
        settingsConfig.saveTheme(theme);
        currentTheme.set(theme);

        SwingUtilities.invokeLater(() -> {
            try {
                switch (theme) {
                    case LIGHT:
                        FlatMacLightLaf.setup();
                        break;
                    case DARK:
                        FlatMacDarkLaf.setup();
                        break;
                }
                
                // Force update of all windows
                for (Window window : Window.getWindows()) {
                    SwingUtilities.updateComponentTreeUI(window);
                    
                    // Ensure any JDialogs are also updated
                    if (window instanceof Frame) {
                        for (Window ownedWindow : window.getOwnedWindows()) {
                            if (ownedWindow instanceof JDialog) {
                                SwingUtilities.updateComponentTreeUI(ownedWindow);
                                
                                // Update components in dialogs
                                for (java.awt.Component comp : ((JDialog) ownedWindow).getContentPane().getComponents()) {
                                    SwingUtilities.updateComponentTreeUI(comp);
                                }
                            }
                        }
                    }
                    
                    // Ensure window is repainted
                    window.repaint();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public Color getThemeIconColor() {
        return getCurrentTheme() == Theme.LIGHT ? Color.BLACK : Color.WHITE;
    }

    public void toggleTheme() {
        Theme newTheme = getCurrentTheme() == Theme.LIGHT ? Theme.DARK : Theme.LIGHT;
        setTheme(newTheme);
    }


}
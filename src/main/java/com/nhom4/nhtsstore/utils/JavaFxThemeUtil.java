package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.enums.Theme;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import javafx.application.Platform;
import javafx.scene.Parent;

import java.util.Objects;

public class JavaFxThemeUtil {

    /**
     * Sets up a theme change listener for JavaFX components
     * Preserves Global.css while adding the appropriate theme CSS
     *
     * @param root The root JavaFX component to style
     * @param themeManager The ThemeManager instance to listen for theme changes
     */
    public static void setupThemeListener(Parent root, ThemeManager themeManager) {
        applyThemeToRoot(root, themeManager.getCurrentTheme());

        themeManager.currentThemeProperty().addListener((obs, oldValue, newValue) -> {
            applyThemeToRoot(root, newValue);
        });
    }

    private static void applyThemeToRoot(Parent root, Theme theme) {
        Platform.runLater(() -> {
            root.getStylesheets().removeIf(style ->
                    style.contains("dark-theme.css") || style.contains("light-theme.css"));

            String themeCss = theme == Theme.DARK
                    ? "/css/dark-theme.css"
                    : "/css/light-theme.css";
            

            root.getStylesheets().add(
                    Objects.requireNonNull(JavaFxThemeUtil.class.getResource(themeCss)).toExternalForm()
            );
        });
    }
}
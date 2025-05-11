package com.nhom4.nhtsstore.configuration.setting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nhom4.nhtsstore.enums.Theme;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SettingsConfig {
    private final String THEME_KEY = "app.theme";
    private final String WINDOW_MODE_KEY = "app.window.mode";
    private final String SCREEN_SIZE_KEY = "app.screen.size";
    private Map<String, Object> userSettings = new HashMap<>();
    private final ObjectMapper objectMapper;
    @Value("${app.default.theme}")
    private String defaultTheme;

    public SettingsConfig() {
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadUserSettings();
    }

    public void saveTheme(Theme theme) {
        saveUserSetting(THEME_KEY, theme.name());
    }

    public Theme getTheme() {
        String themeStr = (String) getUserSetting(THEME_KEY, defaultTheme);
        return Theme.valueOf(themeStr);
    }

    public void saveWindowMode(String windowMode) {
        saveUserSetting(WINDOW_MODE_KEY, windowMode);
    }

    public String getWindowMode() {
        return (String) getUserSetting(WINDOW_MODE_KEY, "Full Screen");
    }

    public void saveScreenSize(String screenSize) {
        saveUserSetting(SCREEN_SIZE_KEY, screenSize);
    }

    public String getScreenSize() {
        return (String) getUserSetting(SCREEN_SIZE_KEY, "1920x1080");
    }

    public void saveUserSetting(String key, Object value) {
        userSettings.put(key, value);
        saveUserSettings();
    }

    public Object getUserSetting(String key, Object defaultValue) {
        return userSettings.getOrDefault(key, defaultValue);
    }

    private File getUserSettingsFile() {
        String userHome = System.getProperty("user.home");
        String appFolder = userHome + File.separator + ".nhtsstore";
        File folder = new File(appFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "user_settings.json");
    }

    private void loadUserSettings() {
        try {
            File file = getUserSettingsFile();
            if (file.exists()) {
                userSettings = objectMapper.readValue(file, Map.class);
            } else {
                // Initialize with default settings from application.yml
                userSettings = new HashMap<>();
                saveUserSettings();
            }
        } catch (IOException e) {
            System.err.println("Failed to load user settings: " + e.getMessage());
            userSettings = new HashMap<>();
        }
    }

    private void saveUserSettings() {
        try {
            objectMapper.writeValue(getUserSettingsFile(), userSettings);
        } catch (IOException e) {
            System.err.println("Failed to save user settings: " + e.getMessage());
        }
    }
}
package com.nhom4.nhtsstore.ui.shared;

import com.nhom4.nhtsstore.configuration.setting.SettingsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LanguageManager {
    private final String LANGUAGE_KEY = "app.language";
    private final SettingsConfig settingsConfig;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;
    private final List<LanguageChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean notifying = new AtomicBoolean(false);
    
    public enum Language {
        ENGLISH("en", "English"),
        VIETNAMESE("vi", "Tiếng Việt");
        
        private final String code;
        private final String displayName;
        
        Language(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static Language fromCode(String code) {
            for (Language language : values()) {
                if (language.getCode().equals(code)) {
                    return language;
                }
            }
            return ENGLISH; // Default to English
        }
    }
    
    @Autowired
    public LanguageManager(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
        
        // Load saved language or default to English
        String savedLanguage = (String) settingsConfig.getUserSetting(LANGUAGE_KEY, Language.ENGLISH.getCode());
        
        // Initial setup without notification
        currentLocale = new Locale(Language.fromCode(savedLanguage).getCode());
        try {
            resourceBundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
        } catch (MissingResourceException e) {
            System.err.println("Failed to load resource bundle: " + e.getMessage());
            // Fallback to default locale
            resourceBundle = ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH);
        }
    }
    
    public void setLanguage(Language language) {
        // If we're already notifying listeners, or the language hasn't changed, do nothing
        if (notifying.get() || (currentLocale != null && currentLocale.getLanguage().equals(language.getCode()))) {
            return;
        }
        
        try {
            currentLocale = new Locale(language.getCode());
            resourceBundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
            
            // Save the language preference
            settingsConfig.saveUserSetting(LANGUAGE_KEY, language.getCode());
            
            // Notify all listeners about the language change
            notifyLanguageChanged();
        } catch (Exception e) {
            System.err.println("Error setting language: " + e.getMessage());
        }
    }
    
    public Language getCurrentLanguage() {
        if (currentLocale == null) {
            return Language.ENGLISH;
        }
        return Language.fromCode(currentLocale.getLanguage());
    }
    
    public String getText(String key) {
        try {
            if (resourceBundle == null) {
                return key;
            }
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println("Missing resource key: " + key);
            return key;
        }
    }
    
    public void addLanguageChangeListener(LanguageChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeLanguageChangeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyLanguageChanged() {
        // Set the notifying flag to prevent recursive calls
        if (notifying.compareAndSet(false, true)) {
            try {
                SwingUtilities.invokeLater(() -> {
                    try {
                        for (LanguageChangeListener listener : listeners) {
                            try {
                                listener.onLanguageChanged();
                            } catch (Exception e) {
                                System.err.println("Error notifying listener: " + e.getMessage());
                            }
                        }
                    } finally {
                        // Reset the notifying flag when done
                        notifying.set(false);
                    }
                });
            } catch (Exception e) {
                // Reset the flag in case of any error
                notifying.set(false);
                System.err.println("Error in notification process: " + e.getMessage());
            }
        }
    }
    
    public interface LanguageChangeListener {
        void onLanguageChanged();
    }
} 
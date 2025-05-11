package com.nhom4.nhtsstore.ui.base;

import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;

/**
 * Base class for UI components that need to support localization.
 * Components that extend this class will automatically receive language change notifications.
 */
public abstract class LocalizableComponent extends JPanel implements LanguageManager.LanguageChangeListener {
    
    protected LanguageManager languageManager;
    
    public LocalizableComponent() {
        // This will be initialized by the updateLanguageManager method called by Spring
    }
    
    /**
     * This method will be called by Spring after bean creation to inject the LanguageManager
     */
    @Autowired
    public void updateLanguageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
        this.languageManager.addLanguageChangeListener(this);
        
        // Initialize localized text if the manager is already available
        if (isInitialized()) {
            onLanguageChanged();
        }
    }
    
    /**
     * Check if the component is fully initialized with required dependencies
     */
    protected boolean isInitialized() {
        return languageManager != null;
    }
    
    /**
     * Called when the language is changed.
     * Components should override this to update their texts.
     */
    @Override
    public void onLanguageChanged() {
        updateTexts();
    }
    
    /**
     * Update all text elements in the component with the current language.
     * Must be implemented by subclasses.
     */
    protected abstract void updateTexts();
    
    /**
     * Helper method to get text from language manager
     */
    protected String getText(String key) {
        if (languageManager == null) {
            return key;
        }
        return languageManager.getText(key);
    }
    
    /**
     * Clean up resources when component is no longer needed
     */
    public void cleanup() {
        if (languageManager != null) {
            languageManager.removeLanguageChangeListener(this);
        }
    }
} 
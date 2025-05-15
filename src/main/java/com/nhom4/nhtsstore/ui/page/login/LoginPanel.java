/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.nhom4.nhtsstore.ui.page.login;


import javax.swing.*;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class LoginPanel extends JPanel implements LanguageManager.LanguageChangeListener{
    private final ApplicationContext applicationContext;
    private final ThemeManager themeManager;
    private JFXPanel jfxPanel;
    private LoginFxController loginFxController;
    
    LoginPanel(ApplicationContext applicationContext, 
              ThemeManager themeManager, 
              LanguageManager languageManager) {
        this.applicationContext = applicationContext;
        this.themeManager = themeManager;
        
        SwingUtilities.invokeLater(() -> {
            Platform.runLater(() -> {
                jfxPanel= JavaFxSwing.createJFXPanelWithController(
                        "/fxml/LoginPanel.fxml",
                        this.applicationContext,
                        (LoginFxController controller) -> {
                            this.loginFxController = controller;
                            loginFxController.setLoginPanel(this);
                        });
                add(jfxPanel);
                
                // Initialize language support after UI is created
//                initializeLanguageSupport();
            });
        });
    }


    @Override
    public void onLanguageChanged() {
        if (loginFxController != null) {
            Platform.runLater(() -> {
                try {
                    loginFxController.updateTexts();
                } catch (Exception e) {
                    System.err.println("Error updating login texts: " + e.getMessage());
                }
            });
        }
    }
}
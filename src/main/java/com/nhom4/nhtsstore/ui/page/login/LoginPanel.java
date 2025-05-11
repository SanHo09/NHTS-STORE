/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.nhom4.nhtsstore.ui.page.login;


import javax.swing.*;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class LoginPanel extends JPanel {
    private final ApplicationContext applicationContext;
    private final ThemeManager themeManager;
    private JFXPanel jfxPanel;
    LoginPanel(ApplicationContext applicationContext, ThemeManager themeManager) {
        this.applicationContext = applicationContext;
        this.themeManager = themeManager;
        SwingUtilities.invokeLater(() -> {
            Platform.runLater(() -> {
                jfxPanel= JavaFxSwing.createJFXPanelWithController(
                        "/fxml/LoginPanel.fxml",
                        this.applicationContext,
                        (LoginFxController loginFxController) -> {
                            loginFxController.setLoginPanel(this);

                        });
                add(jfxPanel);
            });
        });

    }
}
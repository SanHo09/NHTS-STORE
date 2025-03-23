/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.nhom4.nhtsstore.ui.login;

import com.nhom4.nhtsstore.ui.ApplicationState;
import javax.swing.*;

import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.JFXPanel;
import org.springframework.stereotype.Controller;

@Controller
public class LoginPanel extends JPanel {
    private final ApplicationState appState;
    private LoginPanelController controller;

    public LoginPanel(ApplicationState appState) {
        this.appState = appState;
        JFXPanel jfxLoginPanel = JavaFxSwing.createJFXPanelWithController(
                "/fxml/LoginPanel.fxml",
                appState.getApplicationContext(),
                (LoginPanelController c) -> {
                    this.controller = c;
                });
        add(jfxLoginPanel);
    }

    public void resetFields() {
        if (controller != null) {
            javafx.application.Platform.runLater(() -> {
                controller.resetFields();
            });
        }
    }
}

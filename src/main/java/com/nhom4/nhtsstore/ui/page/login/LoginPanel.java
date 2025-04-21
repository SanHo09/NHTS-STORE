/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.nhom4.nhtsstore.ui.page.login;

import com.nhom4.nhtsstore.ui.ApplicationState;
import javax.swing.*;

import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.JFXPanel;
import org.springframework.stereotype.Controller;

@Controller
public class LoginPanel extends JPanel {

    private final ApplicationState appState;

    public LoginPanel(ApplicationState appState) {
        this.appState=appState;

    }
    @PostConstruct
    private void initComponent(){
        JFXPanel jfxLoginPanel = JavaFxSwing.createJFXPanelWithController(
                "/fxml/LoginPanel.fxml",
                appState.getApplicationContext(),
                (LoginPanelController controller) -> {

                });
        add(jfxLoginPanel);
        revalidate();
        repaint();
    }


}
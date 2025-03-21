/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.nhom4.nhtsstore.ui.login;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.ui.ApplicationState;
import javax.swing.*;

import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.JFXPanel;
import org.springframework.stereotype.Controller;
import raven.modal.Toast;

import java.awt.*;

@Controller
public class LoginFrame extends javax.swing.JFrame {

    private final ApplicationState appState;

    public LoginFrame(ApplicationState appState) {
        this.appState=appState;

    }
    @PostConstruct
    private void initComponent(){
        setTitle("Login to NHTS Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set the size maxsize state
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Optional: Use these if you need more control over maximized window bounds
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         setMaximizedBounds(ge.getMaximumWindowBounds());
        setLocationRelativeTo(null);
        setResizable(false);

        // Use the utility class to create the JFXPanel with controller access
        JFXPanel jfxLoginPanel = JavaFxSwing.createJFXPanelWithController(
                "/fxml/LoginPanel.fxml",
                appState.getApplicationContext(),
                (LoginPanelController controller) -> {


                });
        getContentPane().add(jfxLoginPanel);
        appState.authenticatedProperty().addListener((obs, wasAuthenticated, isAuthenticated) -> {
            SwingUtilities.invokeLater(() -> {
                if (isAuthenticated) {
                    setVisible(false);
                    dispose();
                }
            });
        });
    }


}

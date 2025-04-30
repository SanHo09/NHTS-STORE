/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.nhom4.nhtsstore.ui.page.login;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import javax.swing.*;

import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.MsgBox;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Controller
public class LoginPanel extends JPanel implements Initializable {
    private final IUserService userService;
    private final ApplicationState applicationState;
    private final NavigationService navigationService;
    public LoginPanel(UserService userService, ApplicationState applicationState, NavigationService navigationService) {
        this.userService = userService;
        this.applicationState=applicationState;
        this.navigationService = navigationService;
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxLoginPanel = JavaFxSwing.createJFXPanelFromFxml(
                    "/fxml/LoginPanel.fxml",
                    applicationState.getApplicationContext());
            add(jfxLoginPanel);
            revalidate();
            repaint();
        });
    }


    @FXML
    private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXButton loginButton;

    private MFXTooltip usernameTooltip;
    private MFXTooltip passwordTooltip;
    private boolean isLoading = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFields();
        setupTooltips();
//        setupListeners();
    }

    private void setupFields() {
        usernameField.setLeadingIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/HugeiconsMail02.svg", 24, 24, color -> Color.decode("#A280FF")));
        passwordField.setLeadingIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/MaterialSymbolsLockOutline.svg", 24, 24, color ->  Color.decode("#A280FF")));
    }

    private void setupTooltips() {
        Color errorColor = Color.decode("#ef0b0b");
        usernameTooltip = createTooltip(usernameField, "Username is required", errorColor);
        passwordTooltip = createTooltip(passwordField, "Password is required", errorColor);
    }

    private MFXTooltip createTooltip(MFXTextField field, String text, Color iconColor) {
        MFXTooltip tooltip = new MFXTooltip(field);
        tooltip.setContent(field);
        tooltip.setText(text);
        tooltip.setIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/TdesignErrorCircleFilled.svg", 24, 24, color -> iconColor));
        return tooltip;
    }
    private boolean validateInputs() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        boolean valid = true;

        if (username.isEmpty()) {
            showTooltip(usernameField, usernameTooltip);
            valid = false;
        }

        if (password.isEmpty()) {
            showTooltip(passwordField, passwordTooltip);
            valid = false;
        }

        return valid;
    }

    private void showTooltip(MFXTextField field, MFXTooltip tooltip) {
        tooltip.show(field, Alignment.of(HPos.RIGHT, VPos.TOP), 0, -field.getPrefHeight());
    }

    @FXML
    public void submitLogin(MouseEvent actionEvent) {
        if (isLoading) return;
        if (!validateInputs()) return;

        startLoadingState();
        CompletableFuture.runAsync(() -> {
            try {
                var username = usernameField.getText().trim();
                var password = passwordField.getText();
                var userSessionVm = userService.authenticate(username, password);
                Platform.runLater(() -> {
                    applicationState.login(userSessionVm);
                    Toast.show(this, Toast.Type.SUCCESS, "Login successful");
                    resetFields();
                    stopLoadingState();
                    navigationService.navigateTo(AppView.DASHBOARD);
                });
            } catch (AuthenticationException e) {
                Platform.runLater(() -> {
                    if(e.getMessage().equals("User is disabled")) {
                        Toast.show(this, Toast.Type.WARNING,
                                "Your account is disabled. Please contact the administrator.",
                                ToastLocation.TOP_CENTER);
                    } else if (e.getMessage().equals("User account is locked")) {
                        Toast.show(this, Toast.Type.WARNING,
                                "Your account is locked. Please contact the administrator.",
                                ToastLocation.TOP_CENTER);
                    }else if(e.getMessage().equals("Bad credentials")) {
                        Toast.show(this, Toast.Type.WARNING,
                                "Invalid username or password", ToastLocation.TOP_CENTER);
                    }
                    else {
                        Toast.show(this, Toast.Type.WARNING,
                                e.getMessage() , ToastLocation.TOP_CENTER);
                    }
                    stopLoadingState();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    MsgBox.showError("Login Error", e.getMessage());
                    stopLoadingState();
                });
            }
        });
    }



    private void startLoadingState() {
        isLoading = true;
        usernameTooltip.hide();
        passwordTooltip.hide();

        loginButton.setText("Signing in...");
        MFXProgressSpinner spinner = new MFXProgressSpinner();
        spinner.setRadius(10);
        loginButton.setGraphic(spinner);
    }

    private void stopLoadingState() {
        isLoading = false;
        loginButton.setText("Continue");
        loginButton.setGraphic(null);
    }

    //    private void enableLoginButton() {
//        loginButton.setDisable(usernameField.getText().isEmpty() || passwordField.getText().isEmpty());
//    }
    public void resetFields() {
        usernameField.clear();
        passwordField.clear();

        usernameTooltip.hide();
        passwordTooltip.hide();

        stopLoadingState();
    }

}
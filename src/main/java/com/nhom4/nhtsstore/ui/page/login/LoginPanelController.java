package com.nhom4.nhtsstore.ui.page.login;

import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.MsgBox;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import java.awt.Color;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
@Component
public class LoginPanelController  implements Initializable {
    @FXML public MFXTextField usernameField;
    @FXML public MFXPasswordField passwordField;
    @FXML public MFXButton loginButton;

    private MFXTooltip usernameTooltip;
    private MFXTooltip passwordTooltip;
    private boolean isLoading = false;
    private final UserService userService;
    private final ApplicationState applicationState;
    private final LoginPanel loginPanel;

    public LoginPanelController(UserService userService, ApplicationState applicationState) {
        this.userService = userService;
        this.applicationState = applicationState;
        this.loginPanel = applicationState.getApplicationContext().getBean(LoginPanel.class);
    }

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

//    private void setupListeners() {
//        usernameField.textProperty().addListener((o, old, n) -> enableLoginButton());
//        passwordField.textProperty().addListener((o, old, n) -> enableLoginButton());
//    }

    private MFXTooltip createTooltip(MFXTextField field, String text, Color iconColor) {
        MFXTooltip tooltip = new MFXTooltip(field);
        tooltip.setContent(field);
        tooltip.setText(text);
        tooltip.setIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/TdesignErrorCircleFilled.svg", 24, 24, color -> iconColor));
        return tooltip;
    }

    @FXML
    public void submitLogin(MouseEvent actionEvent) {
        if (isLoading) return;
        System.out.println("Login button clicked");
        if (!validateInputs()) return;

        startLoadingState();
        CompletableFuture.runAsync(() -> {
            try {
                var username = usernameField.getText().trim();
                var password = passwordField.getText();
                var userSessionVm = userService.authenticate(username, password);
                Platform.runLater(() -> {
                    if (userSessionVm!=null) {
                        System.out.println("Login user: " + SecurityContextHolder.getContext().getAuthentication().toString());
                        applicationState.login(userSessionVm);
                        Toast.show(loginPanel, Toast.Type.SUCCESS, "Login successful");
                    } else {
                        Toast.show(loginPanel, Toast.Type.WARNING,
                                "Invalid username or password", ToastLocation.TOP_CENTER);
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
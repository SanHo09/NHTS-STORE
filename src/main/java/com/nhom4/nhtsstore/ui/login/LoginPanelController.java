package com.nhom4.nhtsstore.ui.login;

import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
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
import org.springframework.stereotype.Controller;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import java.awt.Color;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static com.nhom4.nhtsstore.utils.JavaFxSwing.createFxImageViewFromSvg;

@Controller
public class LoginPanelController extends StackPane implements Initializable {
    @FXML public MFXTextField usernameField;
    @FXML public MFXPasswordField passwordField;
    @FXML public MFXButton loginButton;

    private MFXTooltip usernameTooltip;
    private MFXTooltip passwordTooltip;
    private boolean isLoading = false;
    private final UserService userService;
    private final ApplicationState applicationState;
    private final LoginFrame loginFrame;

    public LoginPanelController(UserService userService, ApplicationState applicationState) {
        this.userService = userService;
        this.applicationState = applicationState;
        this.loginFrame = applicationState.getApplicationContext().getBean(LoginFrame.class);
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFields();
        setupTooltips();
        setupListeners();
    }

    private void setupFields() {
        usernameField.setLeadingIcon(createFxImageViewFromSvg("/icons/HugeiconsMail02.svg", 24, 24, null));
        passwordField.setLeadingIcon(createFxImageViewFromSvg(
                "/icons/ArcticonsPassword.svg", 24, 24, color -> Color.BLACK));
    }

    private void setupTooltips() {
        Color errorColor = Color.decode("#ef0b0b");
        usernameTooltip = createTooltip(usernameField, "Username is required", errorColor);
        passwordTooltip = createTooltip(passwordField, "Password is required", errorColor);
    }

    private void setupListeners() {
        usernameField.textProperty().addListener((o, old, n) -> enableLoginButton());
        passwordField.textProperty().addListener((o, old, n) -> enableLoginButton());
    }

    private MFXTooltip createTooltip(MFXTextField field, String text, Color iconColor) {
        MFXTooltip tooltip = new MFXTooltip(field);
        tooltip.setContent(field);
        tooltip.setText(text);
        tooltip.setIcon(createFxImageViewFromSvg(
                "/icons/TdesignErrorCircleFilled.svg", 24, 24, color -> iconColor));
        return tooltip;
    }

    @FXML
    public void submitLogin(MouseEvent actionEvent) {
        if (isLoading) return;

        if (!validateInputs()) return;

        startLoadingState();

        // Run authentication in background
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500); // Simulating delay for testing
                var username = usernameField.getText().trim();
                var password = passwordField.getText();
                var userSession = userService.authenticate(username, password);

                Platform.runLater(() -> {
                    if (userSession) {
                        applicationState.login(userService.findByUsername(username));
                        Toast.show(loginFrame, Toast.Type.SUCCESS, "Login successful");
                    } else {
                        Toast.show(loginFrame, Toast.Type.WARNING,
                                "Invalid username or password", ToastLocation.BOTTOM_CENTER);
                    }
                    stopLoadingState();
                });
            } catch (Exception e) {
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

    private void enableLoginButton() {
        loginButton.setDisable(usernameField.getText().isEmpty() || passwordField.getText().isEmpty());
    }
}
package com.nhom4.nhtsstore.ui.page.login;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.MainPanel;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
@Controller
public class LoginFxController extends StackPane implements Initializable {

    private final IUserService userService;
    private final ApplicationState applicationState;
    private final NavigationService navigationService;
    private final ThemeManager themeManager;
    @Setter
    private JPanel loginPanel;
    public LoginFxController(UserService userService, ApplicationState applicationState, NavigationService navigationService, ThemeManager themeManager) {
        this.userService = userService;
        this.applicationState=applicationState;
        this.navigationService = navigationService;
        this.themeManager = themeManager;
    }

    @FXML
    private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXButton loginButton;
    @FXML private VBox root;
    private MFXTooltip usernameTooltip;
    private MFXTooltip passwordTooltip;
    private boolean isLoading = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFields();
        setupTooltips();
        JavaFxThemeUtil.setupThemeListener(root, themeManager);

    }

    private void setupFields() {
        usernameField.setLeadingIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/HugeiconsMail02.svg", 20, 20, color -> Color.decode("#A280FF")));
        passwordField.setLeadingIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/MaterialSymbolsLockOutline.svg", 20, 20, color ->  Color.decode("#A280FF")));
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
                    Toast.show(loginPanel, Toast.Type.SUCCESS, "Login successful");
                    resetFields();
                    stopLoadingState();
                    navigationService.navigateTo(AppView.DASHBOARD);
                });
            } catch (AuthenticationException e) {
                Platform.runLater(() -> {
                    if(e.getMessage().equals("User is disabled")) {
                        Toast.show(loginPanel, Toast.Type.WARNING,
                                "Your account is disabled. Please contact the administrator.",
                                ToastLocation.TOP_CENTER);
                    } else if (e.getMessage().equals("User account is locked")) {
                        Toast.show(loginPanel, Toast.Type.WARNING,
                                "Your account is locked. Please contact the administrator.",
                                ToastLocation.TOP_CENTER);
                    }else if(e.getMessage().equals("Bad credentials")) {
                        Toast.show(loginPanel, Toast.Type.WARNING,
                                "Invalid username or password", ToastLocation.TOP_CENTER);
                    }
                    else {
                        Toast.show(loginPanel, Toast.Type.WARNING,
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
        loginButton.setText("Login");
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

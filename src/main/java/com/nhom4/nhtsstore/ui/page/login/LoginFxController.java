package com.nhom4.nhtsstore.ui.page.login;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.services.impl.UserService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class LoginFxController extends StackPane implements Initializable {

    private final IUserService userService;
    private final ApplicationState applicationState;
    private final NavigationService navigationService;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    
    @Setter
    private JPanel loginPanel;
    
    public LoginFxController(UserService userService, 
                            ApplicationState applicationState, 
                            NavigationService navigationService, 
                            ThemeManager themeManager,
                            LanguageManager languageManager) {
        this.userService = userService;
        this.applicationState = applicationState;
        this.navigationService = navigationService;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
    }

    @FXML
    private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXButton loginButton;
    @FXML private VBox root;
    private MFXTooltip usernameTooltip;
    private MFXTooltip passwordTooltip;
    private boolean isLoading = false;
    private ExecutorService executorService= Executors.newVirtualThreadPerTaskExecutor();
    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFields();
        setupTooltips();
        JavaFxThemeUtil.setupThemeListener(root, themeManager);
        
        // Initial text update
        updateTexts();
    }

    /**
     * Update all text elements with localized strings
     */
    public void updateTexts() {
        if (languageManager == null) return;
        
        Platform.runLater(() -> {
            try {
                // Update field prompts
                if (usernameField != null) {
                    usernameField.setFloatingText(languageManager.getText("login.username"));
                    usernameField.setPromptText(languageManager.getText("login.username.prompt"));
                }
                
                if (passwordField != null) {
                    passwordField.setFloatingText(languageManager.getText("login.password"));
                    passwordField.setPromptText(languageManager.getText("login.password.prompt"));
                }
                
                // Update button text
                if (loginButton != null && !isLoading) {
                    loginButton.setText(languageManager.getText("login.button"));
                }
                
                // Update tooltips
                if (usernameTooltip != null) {
                    usernameTooltip.setText(languageManager.getText("login.username.required"));
                }
                
                if (passwordTooltip != null) {
                    passwordTooltip.setText(languageManager.getText("login.password.required"));
                }
            } catch (Exception e) {
                System.err.println("Error updating login texts: " + e.getMessage());
            }
        });
    }

    private void setupFields() {
        usernameField.setLeadingIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/MaterialSymbolsAccountBox.svg", 20, 20, color -> Color.decode("#0f156d")));
        passwordField.setLeadingIcon(IconUtil.createFxImageViewFromSvg(
                "/icons/MaterialSymbolsLockOutline.svg", 20, 20, color ->  Color.decode("#0f156d")));
    }

    private void setupTooltips() {
        Color errorColor = Color.decode("#ef0b0b");
        usernameTooltip = createTooltip(usernameField, 
            languageManager != null ? languageManager.getText("login.username.required") : "Username is required", 
            errorColor);
            
        passwordTooltip = createTooltip(passwordField, 
            languageManager != null ? languageManager.getText("login.password.required") : "Password is required", 
            errorColor);
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
        executorService.submit(() -> {
            try {
                var username = usernameField.getText().trim();
                var password = passwordField.getText();
                var userSessionVm = userService.authenticate(username, password);
                Platform.runLater(() -> {
                    applicationState.login(userSessionVm);
                    String successMessage = languageManager != null ?
                            languageManager.getText("login.success") : "Login successful";
                    Toast.show(loginPanel, Toast.Type.SUCCESS, successMessage);
                    resetFields();
                    stopLoadingState();
                    navigationService.navigateTo(AppView.DASHBOARD);
                });
            } catch (AuthenticationException e) {
                Platform.runLater(() -> {
                    String errorMessage;
                    if (e.getMessage().equals("User is disabled")) {
                        errorMessage = languageManager != null ?
                                languageManager.getText("login.error.disabled") :
                                "Your account is disabled. Please contact the administrator.";
                    } else if (e.getMessage().equals("User account is locked")) {
                        errorMessage = languageManager != null ?
                                languageManager.getText("login.error.locked") :
                                "Your account is locked. Please contact the administrator.";
                    } else if (e.getMessage().equals("Bad credentials")) {
                        errorMessage = languageManager != null ?
                                languageManager.getText("login.error.credentials") :
                                "Invalid username or password";
                    } else {
                        errorMessage = e.getMessage();
                    }

                    Toast.show(loginPanel, Toast.Type.WARNING, errorMessage, ToastLocation.TOP_CENTER);
                    stopLoadingState();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    MsgBox.showError(
                            languageManager != null ? languageManager.getText("login.error.title") : "Login Error",
                            e.getMessage()
                    );
                    stopLoadingState();
                });
            }
        });
//        CompletableFuture.runAsync(() -> {
//
//        });
    }

    private void startLoadingState() {
        isLoading = true;
        usernameTooltip.hide();
        passwordTooltip.hide();

        loginButton.setText(languageManager != null ? 
            languageManager.getText("login.signingin") : "Signing in...");
        MFXProgressSpinner spinner = new MFXProgressSpinner();
        spinner.setRadius(10);
        loginButton.setGraphic(spinner);
    }

    private void stopLoadingState() {
        isLoading = false;
        loginButton.setText(languageManager != null ? 
            languageManager.getText("login.button") : "Login");
        loginButton.setGraphic(null);
    }

    public void resetFields() {
        usernameField.clear();
        passwordField.clear();

        usernameTooltip.hide();
        passwordTooltip.hide();

        stopLoadingState();
    }
}

package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


@Controller
public class Header extends StackPane implements Initializable, LanguageManager.LanguageChangeListener {
    private final ApplicationState applicationState;
    private final NavigationService navigationService;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    @FXML private ImageView logoutIcon;
    @FXML private ImageView profileIcon;
    @FXML
    private BorderPane root;
    @FXML
    private MenuButton dropDownMenu;

    @FXML
    private ImageView userAvatarImage;

    @FXML
    private MenuItem menuItemProfile;

    @FXML
    private MenuItem menuItemLogout;

    @FXML
    private Label clockLabel;

    private Timeline clockTimeline;
    private DateTimeFormatter timeFormatter;
    private String dateTimePattern;

    public Header(ApplicationState applicationState, PanelManager panelManager, NavigationService navigationService, ThemeManager themeManager, LanguageManager languageManager) {
        this.applicationState = applicationState;
        this.navigationService = navigationService;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure we're on the JavaFX thread for all UI updates
        Platform.runLater(() -> {
            applicationState.currentUserProperty().addListener((obs, oldValue, newValue) -> 
                Platform.runLater(() -> updateUserDisplay(newValue)));
            
            // Register for language changes
            languageManager.addLanguageChangeListener(this);
            
            // Update menu item text
            updateMenuTexts();
            logoutIcon.setImage(IconUtil.createFxImageFromSvg("/icons/MaterialSymbolsLogout.svg", 20, 20, color -> Color.decode("#0f156d")));
            profileIcon.setImage( IconUtil.createFxImageFromSvg("/icons/MaterialSymbolsArticlePerson.svg", 20, 20, color -> Color.decode("#0f156d")));
            initializeClock();
            
            // Apply theme and listen for theme changes
            JavaFxThemeUtil.setupThemeListener(root, themeManager);
            

        });
    }
    
    private void updateMenuTexts() {
        // Ensure we're on the JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::updateMenuTexts);
            return;
        }
        
        try {
            menuItemProfile.setText(languageManager.getText("nav.user_profile"));
            menuItemLogout.setText(languageManager.getText("nav.logout"));
        } catch (Exception e) {
            System.err.println("Error updating menu texts: " + e.getMessage());
        }
    }

    private void initializeClock() {
        updateDateTimeFormatter();

        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        clockTimeline.setCycleCount(Animation.INDEFINITE);

        updateClock();
        clockTimeline.play();
    }
    
    private void updateDateTimeFormatter() {
        // Get the date format pattern from the language manager
        dateTimePattern = languageManager.getText("datetime.format");
        timeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
    }
    
    private void updateClock() {
        // Ensure we're on the JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::updateClock);
            return;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText(timeFormatter.format(now));
        } catch (Exception e) {
            System.err.println("Error updating clock: " + e.getMessage());
        }
    }

    private void updateUserDisplay(UserSessionVm user) {
        // Ensure we're on the JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> updateUserDisplay(user));
            return;
        }
        
        try {
            if (user != null) {
                String initial = user.getFullName();
                dropDownMenu.setText(initial);
            } else {
                dropDownMenu.setText("User");
            }
        } catch (Exception e) {
            System.err.println("Error updating user display: " + e.getMessage());
        }
    }

    @FXML
    private void onActionMenuItemProfile() {
        navigateToProfile();
    }

    @FXML
    private void onActionMenuItemLogout() {
        logout();
    }

    private void navigateToProfile() {
        // Navigate to the profile view
        RouteParams params = new RouteParams();
        params.set("userId", applicationState.getCurrentUser().getUserId());
        navigationService.navigateTo(AppView.USER_PROFILE, params);
    }

    private void logout() {
        // Stop the clock timeline to prevent memory leaks
        if (clockTimeline != null) {
            clockTimeline.stop();
        }

        applicationState.logout();
    }
    
    @Override
    public void onLanguageChanged() {
        // We need to update UI elements on the JavaFX thread
        Platform.runLater(() -> {
            try {
                // Update the date formatter with the new language
                updateDateTimeFormatter();
                
                // Update the clock immediately
                updateClock();
                
                // Update menu items
                updateMenuTexts();
            } catch (Exception e) {
                System.err.println("Error in language change handler: " + e.getMessage());
            }
        });
    }

}
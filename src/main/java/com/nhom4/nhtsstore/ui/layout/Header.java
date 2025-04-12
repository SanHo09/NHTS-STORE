package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.utils.PanelManager;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;


@Controller
public class Header extends StackPane implements Initializable {
    private final ApplicationState applicationState;
    private final PanelManager panelManager;
    @FXML
    private MenuButton dropDownMenu;

    @FXML
    private ImageView userAvatarImage;

    @FXML
    private MenuItem menuItemProfile;

    @FXML
    private MenuItem menuItemLogout;

    public Header(ApplicationState applicationState, PanelManager panelManager) {
        this.applicationState = applicationState;
        this.panelManager = panelManager;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Existing icon setup code


        applicationState.currentUserProperty().addListener((obs, oldValue, newValue) -> updateUserDisplay(newValue));
    }



    private void updateUserDisplay(UserSessionVm user) {
        if (user != null) {
            // Update avatar or user display
            String initial = user.getFullName();
//            userAvatarImage.setImage(new Image(user.getAvatarUrl()));
            dropDownMenu.setText(initial);
        } else {
            dropDownMenu.setText("User");
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
//        panelManager.navigateTo(ViewName.PROFILE_VIEW,
//                applicationState.getViewPanelByBean(ViewName.PROFILE_VIEW.getPanelClass()));
    }

    private void logout() {
        // Perform logout and navigate to login view
        applicationState.logout();

    }
}

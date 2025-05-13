package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.swing.JPanel;
import java.awt.BorderLayout;

@Controller
@Scope("prototype")
public class UserPanel extends JPanel implements RoutablePanel {

	private static final long serialVersionUID = 1L;
	
	private final IUserService userService;
	private final ThemeManager themeManager;
	private final LanguageManager languageManager;
	private final NavigationService navigationService;
	private final PanelManager panelManager;
	private final ApplicationContext applicationContext;
	private final UserListFxController userListFxController;
	@Autowired
	public UserPanel(
            IUserService userService,
            ThemeManager themeManager,
            LanguageManager languageManager,
            NavigationService navigationService,
            PanelManager panelManager,
            ApplicationContext applicationContext, UserListFxController userListFxController) {
	    this.userService = userService;
	    this.themeManager = themeManager;
	    this.languageManager = languageManager;
	    this.navigationService = navigationService;
	    this.panelManager = panelManager;
	    this.applicationContext = applicationContext;
        this.userListFxController = userListFxController;

        initComponents();
	}
	
	private void initComponents() {
	    setLayout(new BorderLayout());
	    
	    // Create the JFXPanel and add it to this panel
	    add(JavaFxSwing.createJFXPanelWithController(
	            "/fxml/UserListPanel.fxml",
	            applicationContext,
	            true,
	            (UserListFxController controller) -> {
                    // Set callbacks for user actions
                    controller.setOnEditUser(this::handleEditUser);
                    controller.setOnCreateUser(this::handleCreateUser);
					controller.setOnDeleteUser(user -> {
						// Handle delete user action
						userService.deleteUser(user.getUserId());
					});

                }
	    ), BorderLayout.CENTER);
	}
	
	private void handleEditUser(UserRecordVm user) {
	    // Navigate to edit user page
	    RouteParams params = new RouteParams();
	    params.set("userId", user.getUserId());
	    navigationService.navigateTo(UserProfilePanel.class, params);
	}
	
	private void handleCreateUser() {
	    // Navigate to create user page
	    navigationService.navigateTo(UserCreatePanel.class, new RouteParams());
	}

	@Override
	public void onNavigate(RouteParams params) {
		userListFxController.onNavigate();
	}
}

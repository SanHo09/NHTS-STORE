package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.layout.Header;
import com.nhom4.nhtsstore.ui.layout.Menu;
import com.nhom4.nhtsstore.ui.layout.PagePanel;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.shared.components.GlobalLoadingManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

@Component
public class MainPanel extends JPanel {
	private final ApplicationState applicationState;
	private final ApplicationContext applicationContext;
	private final NavigationService navigationService;
	private final PagePanel pagePanel;
	private final Menu menu;
	private JPanel mainContentPanel;

	public MainPanel(ApplicationState applicationState, ApplicationContext applicationContext,
					 PagePanel pagePanel, Menu menu, NavigationService navigationService) {
		this.applicationState = applicationState;
		this.applicationContext = applicationContext;
		this.pagePanel = pagePanel;
		this.menu = menu;
		this.navigationService = navigationService;
		setLayout(new BorderLayout());
		menu.initMoving(this);
		menu.addEventMenuSelected(index -> {
			AppView[] appViews = AppView.values();
			int menuPosition = 0;
			for (AppView appView : appViews) {
				if (appView == AppView.LOGIN) {
					continue; // Skip LOGIN
				}
				if (menuPosition == index) {
					// Navigate to the selected view
					navigationService.navigateTo(appView, new RouteParams());
					break;
				}
				menuPosition++;
			}
		});

		// Add listener to detect when user logs in
		applicationState.authenticatedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				// User just logged in - refresh menu
				SwingUtilities.invokeLater(this::refreshMenu);
			}
		});
	}
	private void refreshMenu() {
		// Clear and rebuild the menu based on current user role
		menu.refreshMenuItems();

		// Navigate to default view (typically dashboard)
		navigationService.navigateTo(AppView.DASHBOARD, new RouteParams());
	}

	@PostConstruct
	private void initializeComponents() {
		JLayeredPane layeredPagePanel;

		add(JavaFxSwing.createJFXPanelWithController(
				"/fxml/HeaderLayout.fxml",
				this.applicationContext,
				true,
				(Header header) -> {
				}), BorderLayout.NORTH);
		mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.add(menu, BorderLayout.WEST);

		layeredPagePanel = new JLayeredPane();
		layeredPagePanel.setLayout(new OverlayLayout(layeredPagePanel));
		layeredPagePanel.add(pagePanel, 0); // pagePanel nằm dưới
		layeredPagePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		mainContentPanel.add(layeredPagePanel, BorderLayout.CENTER);
		pagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // add padding

		add(mainContentPanel, BorderLayout.CENTER);

		GlobalLoadingManager.getInstance().init(layeredPagePanel);
	}
}

package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.layout.Header;
import com.nhom4.nhtsstore.ui.layout.Menu;
import com.nhom4.nhtsstore.ui.layout.PagePanel;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class MainPanel extends JPanel {
	private final ApplicationState applicationState;
	private final ApplicationContext applicationContext;
	private final PanelManager panelManager;
	private final PagePanel pagePanel;
	private final Menu menu;
	private final NavigationService navigationService;
	private JPanel mainContentPanel;

	public MainPanel(ApplicationState applicationState, ApplicationContext applicationContext, PanelManager panelManager,
                     PagePanel pagePanel, Menu menu, NavigationService navigationService) {
		this.applicationState = applicationState;
        this.applicationContext = applicationContext;
        this.panelManager = panelManager;
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
					panelManager.navigateTo(appView,
							applicationState.getViewPanelByBean(appView.getPanelClass()));
					break;
				}
				menuPosition++;
			}
		});

	}

	@PostConstruct
	private void initializeComponents() {
		add(JavaFxSwing.createJFXPanelWithController(
				"/fxml/HeaderLayout.fxml",
				this.applicationContext,
				true,
				(Header header) -> {

				}), BorderLayout.NORTH);
		mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.add(menu, BorderLayout.WEST);
		mainContentPanel.add(pagePanel, BorderLayout.CENTER);
		add(mainContentPanel, BorderLayout.CENTER);

		// Set the default view to DASHBOARD
		panelManager.navigateTo(AppView.DASHBOARD,
				applicationState.getViewPanelByBean(DashBoardPanel.class));

		//test


	}
}

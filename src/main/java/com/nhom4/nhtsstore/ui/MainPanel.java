package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.layout.Header;
import com.nhom4.nhtsstore.ui.layout.Menu;
import com.nhom4.nhtsstore.ui.layout.PagePanel;
import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.PanelManager;
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
	private final Header header;

	private JPanel mainContentPanel;

	public MainPanel(ApplicationState applicationState, ApplicationContext applicationContext, PanelManager panelManager,
                     PagePanel pagePanel, Menu menu, Header header) {
		this.applicationState = applicationState;
        this.applicationContext = applicationContext;
        this.panelManager = panelManager;
		this.pagePanel = pagePanel;
		this.menu = menu;
		this.header = header;
		setLayout(new BorderLayout());
		menu.initMoving(this);
		menu.addEventMenuSelected(index -> {
			AppView[] appViews = AppView.values();
			int menuPosition = 0;
			for (AppView appView : appViews) {
				if (appView == AppView.LOGIN) {
					continue; // Skip LOGIN
				}
				if (appView.getParent() != null) {
					continue; // Skip submenu items for now
				}
				if (menuPosition == index) {
					if (appView.getPanelClass() != null) {
						panelManager.navigateTo(
								appView,
								applicationState.getViewPanelByBean(appView.getPanelClass())
						);
					}
					break;
				}
				menuPosition++;
				// Handle submenu items
				for (AppView subView : appViews) {
					if (subView.getParent() == appView) {
						menuPosition++;
						if (menuPosition == index && subView.getPanelClass() != null) {
							panelManager.navigateTo(
									subView,
									applicationState.getViewPanelByBean(subView.getPanelClass())
							);
							break;
						}
					}
				}
			}
		});
		// Set the default view to DASHBOARD
		panelManager.navigateTo(AppView.DASHBOARD,
				applicationState.getViewPanelByBean(DashBoardPanel.class));

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


	}
}

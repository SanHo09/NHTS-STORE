package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.layout.Header;
import com.nhom4.nhtsstore.ui.layout.Menu;
import com.nhom4.nhtsstore.ui.layout.PagePanel;
import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.EventMenuSelected;
import com.nhom4.nhtsstore.utils.PanelManager;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class MainPanel extends JPanel {
	private final ApplicationState applicationState;
	private final PanelManager panelManager;
	private final PagePanel pagePanel;
	private final Menu menu;
	private final Header header;

	private JPanel mainContentPanel;

	public MainPanel(ApplicationState applicationState, PanelManager panelManager,
					 PagePanel pagePanel, Menu menu, Header header) {
		this.applicationState = applicationState;
		this.panelManager = panelManager;
		this.pagePanel = pagePanel;
		this.menu = menu;
		this.header = header;
		setLayout(new BorderLayout());
		menu.initMoving(this);
		menu.addEventMenuSelected(index -> {
			ViewName[] viewNames = ViewName.values();
			int menuPosition = 0;
			for (ViewName viewName : viewNames) {
				// Skip LOGIN_VIEW as it's not in the menu
				if (viewName == ViewName.LOGIN_VIEW) {
					continue;
				}
				// Found the selected menu item
				if (menuPosition == index) {
					if (viewName.getPanelClass() != null) {
						panelManager.navigateTo(
								viewName,
								applicationState.getViewPanelByBean(viewName.getPanelClass())
						);
					}
					break;
				}
				menuPosition++;
			}
		});
		// Set the default view to DASHBOARD_VIEW
		panelManager.navigateTo(ViewName.DASHBOARD_VIEW,
				applicationState.getViewPanelByBean(DashBoardPanel.class));

	}

	@PostConstruct
	private void initializeComponents() {
		add(menu, BorderLayout.WEST);
		mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.add(header, BorderLayout.NORTH);
		mainContentPanel.add(pagePanel, BorderLayout.CENTER);
		add(mainContentPanel, BorderLayout.CENTER);
		// Set default view when authenticated
		if (applicationState.isAuthenticated() &&
				applicationState.currentViewProperty().get() == null) {
			panelManager.navigateTo(ViewName.DASHBOARD_VIEW,
					applicationState.getViewPanelByBean(DashBoardPanel.class));
		}

	}
}
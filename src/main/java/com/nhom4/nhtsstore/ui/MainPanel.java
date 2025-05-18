/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.nhom4.nhtsstore.ui;


import com.nhom4.nhtsstore.ui.layout.PagePanel;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.components.GlobalLoadingManager;
import com.nhom4.nhtsstore.ui.shared.components.sidebar.SidebarFxController;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.JFXPanel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

@Controller
public class MainPanel extends JPanel {
	private final ApplicationState applicationState;
	private final ApplicationContext applicationContext;
	private final NavigationService navigationService;
	private final PagePanel pagePanel;
	private SidebarFxController sidebarController;
	private JPanel mainContentPanel;

	public MainPanel(ApplicationState applicationState, ApplicationContext applicationContext,
					 PagePanel pagePanel, NavigationService navigationService) {
		this.applicationState = applicationState;
		this.applicationContext = applicationContext;
		this.pagePanel = pagePanel;
		this.navigationService = navigationService;
		setLayout(new BorderLayout());


	}


	@PostConstruct
	private void initializeComponents() {
		JLayeredPane layeredPagePanel;

		add(JavaFxSwing.createJFXPanelFromFxml(
				"/fxml/HeaderLayout.fxml",
				this.applicationContext), BorderLayout.NORTH);
		
		mainContentPanel = new JPanel(new BorderLayout());
		
		// Create JavaFX sidebar instead of Swing one
		JFXPanel sidebarPanel = JavaFxSwing.createJFXPanelWithController(
			"/fxml/SidebarMenu.fxml",
			this.applicationContext,
			false,
			(SidebarFxController controller) -> {
				this.sidebarController = controller;
			}
		);
		
		// Set preferred width for sidebar
		sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
		mainContentPanel.add(sidebarPanel, BorderLayout.WEST);

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

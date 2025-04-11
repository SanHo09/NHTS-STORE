package com.nhom4.nhtsstore.ui.layout;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class PagePanel extends JPanel {
	private final CardLayout cardLayout;

	public PagePanel() {
		cardLayout = new CardLayout();
		setLayout(cardLayout);
	}

	public void showPanel(JPanel panel) {
		// Remove all previous components
		removeAll();

		// Add the new panel
		add(panel, panel.getClass().getName());

		// Show the panel
		revalidate();
		repaint();
	}
}
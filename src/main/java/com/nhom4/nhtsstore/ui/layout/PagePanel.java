package com.nhom4.nhtsstore.ui.layout;

import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class PagePanel extends JPanel {

	public PagePanel() {
		setLayout(new BorderLayout());
	}

	public void showPanel(JPanel panel) {
		removeAll();

		add(panel, BorderLayout.CENTER);


		// Show the panel
		revalidate();
		repaint();

	}
}
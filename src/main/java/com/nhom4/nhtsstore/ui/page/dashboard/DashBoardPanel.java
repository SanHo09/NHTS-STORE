package com.nhom4.nhtsstore.ui.page.dashboard;

import org.springframework.stereotype.Controller;

import javax.swing.*;

@Controller
public class DashBoardPanel extends JPanel {
    public DashBoardPanel() {
        initComponents();
    }

    private void initComponents() {
        setVisible(true);
        // Add your dashboard components here
        JLabel label = new JLabel("Dashboard");
        add(label);
    }
}

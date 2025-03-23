package com.nhom4.nhtsstore.ui.dashboard;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.swing.*;

@Controller
public class DashBoardPanel extends JPanel {
    public DashBoardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Dashboard"));
    }
}

package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;

@Controller
public class DashBoardPanel extends JPanel {
    private final NavigationService navigationService;
    private final DashBoardFxController dashBoardFxController;
    public DashBoardPanel(NavigationService navigationService, DashBoardFxController dashBoardFxController) {
        this.navigationService = navigationService;
        this.dashBoardFxController = dashBoardFxController;
        setLayout(new BorderLayout(5, 5));
        initComponents();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        add(dashBoardFxController, BorderLayout.CENTER);
    }
}

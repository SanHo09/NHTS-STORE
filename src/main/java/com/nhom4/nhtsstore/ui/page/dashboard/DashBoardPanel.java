package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import org.springframework.stereotype.Controller;

import javax.swing.*;

@Controller
public class DashBoardPanel extends JPanel {
    private final NavigationService navigationService;
    public DashBoardPanel(NavigationService navigationService) {
        this.navigationService = navigationService;
        initComponents();
    }

    private void initComponents() {
        setVisible(true);
        JLabel label = new JLabel("Dashboard");
        add(label);
        JButton testLoadUser = new JButton("Test Load User");

        testLoadUser.addActionListener(e -> {
            RouteParams params = new RouteParams();
            params.set("userId", 3L);

            // Navigate to profile with parameters
            navigationService.navigateTo(AppView.USER_PROFILE, params);
        });
        add(testLoadUser);
    }
}

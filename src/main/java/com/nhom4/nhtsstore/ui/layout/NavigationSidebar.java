package com.nhom4.nhtsstore.ui.layout;

import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.ViewName;
import com.nhom4.nhtsstore.ui.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.utils.PanelManager;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

@Component
public class NavigationSidebar extends JPanel {
    private final PanelManager panelManager;
    private final ApplicationState applicationState;
    private final Map<ViewName, JButton> navButtons = new HashMap<>();

    public NavigationSidebar(PanelManager panelManager, ApplicationState applicationState) {
        this.panelManager = panelManager;
        this.applicationState = applicationState;

        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        setupLayout();

        // Listen for view changes to update active button
        applicationState.currentViewProperty().addListener((obs, oldVal, newVal) -> {
            highlightActiveView(newVal);
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Logo area
        JLabel logo = new JLabel("NHTS", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setPreferredSize(new Dimension(0, 60));
        logo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(logo, BorderLayout.NORTH);

        // Navigation menu
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        navPanel.setOpaque(false);

        // Create navigation buttons for each view
        addNavigationButton(navPanel, ViewName.DASHBOARD_VIEW, "Dashboard");
        addNavigationButton(navPanel, ViewName.PRODUCT_VIEW, "Products");
//        addNavigationButton(navPanel, ViewName.CATEGORIES, "Categories");
//        addNavigationButton(navPanel, ViewName.ORDERS, "Orders");
//        addNavigationButton(navPanel, ViewName.CUSTOMERS, "Customers");
//        addNavigationButton(navPanel, ViewName.SETTINGS, "Settings");

        // Add some vertical spacing
        navPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize active view
        if (applicationState.currentViewProperty().get() != null) {
            highlightActiveView(applicationState.currentViewProperty().getValue());
        }
    }

    private void addNavigationButton(JPanel container, ViewName viewName, String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.addActionListener(e -> handleNavigation(viewName));

        container.add(button);
        container.add(Box.createRigidArea(new Dimension(0, 5)));

        navButtons.put(viewName, button);
    }

    private void handleNavigation(ViewName viewName) {
        Class<?> panelClass = getPanelClassForView(viewName);
        if (panelClass != null) {
            panelManager.navigateTo(viewName, applicationState.getViewPanelByBean(panelClass));
        }
    }

    private void highlightActiveView(ViewName viewName) {
        // Reset all buttons to default style
        navButtons.forEach((view, button) -> {
            button.setBackground(UIManager.getColor("Button.background"));
            button.setForeground(UIManager.getColor("Button.foreground"));
            button.setFont(button.getFont().deriveFont(Font.PLAIN));
        });

        // Highlight active button
        if (navButtons.containsKey(viewName)) {
            JButton activeButton = navButtons.get(viewName);
            activeButton.setBackground(new Color(66, 139, 202));
            activeButton.setForeground(Color.WHITE);
            activeButton.setFont(activeButton.getFont().deriveFont(Font.BOLD));
        }
    }
    private Class<?> getPanelClassForView(ViewName viewName) {
        switch (viewName) {
            case DASHBOARD_VIEW:
                return DashBoardPanel.class;
//            case PRODUCTS_VIEW:
//                return ProductsPanel.class;
//            case CATEGORIES_VIEW:
//                return CategoriesPanel.class;
//            case ORDERS_VIEW:
//                return OrdersPanel.class;
//            case CUSTOMERS_VIEW:
//                return CustomersPanel.class;
//            case SETTINGS_VIEW:
//                return SettingsPanel.class;
            default:
                return null;
        }
    }
}
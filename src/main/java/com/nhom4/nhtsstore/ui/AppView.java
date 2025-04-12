package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import lombok.Getter;

import javax.swing.*;

@Getter
public enum AppView {
    LOGIN("Login", "", null, LoginPanel.class),
    DASHBOARD("Dashboard", "", null, DashBoardPanel.class),
    PRODUCT("Product", "category", null, Form_Home.class),
    CATEGORY("Category", "category", PRODUCT, Form_Home.class),
    SUPPLIER("Supplier", "", null, Form_1.class),
    USER("User", "user", null, Form_2.class),
    CUSTOMER("Customer", "user", USER, null),
    SETTING("Setting", "hammer", null, null);

    private final String name;
    private final String icon;
    private final AppView parent; // for submenus
    private final Class<? extends JPanel> panelClass;

    AppView(String name, String icon, AppView parent, Class<? extends JPanel> panelClass) {
        this.name = name;
        this.icon = icon;
        this.parent = parent;
        this.panelClass = panelClass;
    }

}

package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import com.nhom4.nhtsstore.ui.page.permission.PermissionListPanel;
import com.nhom4.nhtsstore.ui.page.role.RoleListPanel;
import com.nhom4.nhtsstore.ui.page.user.UserProfilePanel;
import com.nhom4.nhtsstore.ui.page.product.ProductListPanel;
import lombok.Getter;
import javax.swing.*;

@Getter
public enum AppView {
    LOGIN("Login", "", null, LoginPanel.class, null),
    DASHBOARD("Dashboard", "", null, DashBoardPanel.class, null),
    PRODUCT("Product", "BoxSeamFill.svg", null, ProductListPanel.class, null),
    CATEGORY("Category", "category.png", PRODUCT, Form_Home.class, null),
    SUPPLIER("Supplier", "", null, Form_1.class, null),
    USER("User", "BiPersonBoundingBox.svg", null, Form_2.class, "SUPER_ADMIN"),
    USER_PROFILE("User Profile", "", USER, UserProfilePanel.class, null),
    ROLE("Role", "", USER, RoleListPanel.class, "SUPER_ADMIN"),
    PERMISSION("Permission", "", USER, PermissionListPanel.class, "SUPER_ADMIN"),
    CUSTOMER("Customer", "user.png", USER, null, null),
    SETTING("Setting", "hammer.png", null, null, null);

    private final String name;
    private final String icon;
    private final AppView parent; // for submenus
    private final Class<? extends JPanel> panelClass;
    private final String requiredRole; // The role required to access this view

    AppView(String name, String icon, AppView parent, Class<? extends JPanel> panelClass, String requiredRole) {
        this.name = name;
        this.icon = icon;
        this.parent = parent;
        this.panelClass = panelClass;
        this.requiredRole = requiredRole;
    }

    public boolean requiresSuperAdmin() {
        return "SUPER_ADMIN".equals(requiredRole);
    }
}
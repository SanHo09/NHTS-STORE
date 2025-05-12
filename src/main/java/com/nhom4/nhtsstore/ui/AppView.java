package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.page.customer.CustomerListPanel;
import com.nhom4.nhtsstore.ui.pointOfSale.CartPanel;
import com.nhom4.nhtsstore.ui.pointOfSale.PointOfSalePanel;
import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.page.invoice.InvoiceListPanel;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import com.nhom4.nhtsstore.ui.page.order.OrderListPanel;
import com.nhom4.nhtsstore.ui.page.permission.PermissionListPanel;
import com.nhom4.nhtsstore.ui.page.role.RoleListPanel;
import com.nhom4.nhtsstore.ui.page.setting.SettingPanel;
import com.nhom4.nhtsstore.ui.page.user.UserProfilePanel;
import com.nhom4.nhtsstore.ui.page.product.ProductListPanel;
import com.nhom4.nhtsstore.ui.page.productCategory.ProductCategoryListPanel;
import com.nhom4.nhtsstore.ui.page.supplier.SupplierListPanel;
import com.nhom4.nhtsstore.ui.page.supplierCategory.SupplierCategoryListPanel;
import com.nhom4.nhtsstore.ui.page.user.UserPanel;
import lombok.Getter;
import javax.swing.*;

@Getter
public enum AppView {
    LOGIN("Login", "", null, LoginPanel.class, null),
    DASHBOARD("Dashboard", "Speedometer2.svg", null, DashBoardPanel.class, null),
    PRODUCT("Product", "BoxSeamFill.svg", null, ProductListPanel.class, null),
    CATEGORY("Product Category", "", PRODUCT, ProductCategoryListPanel.class, null),
    SUPPLIER("Supplier", "Truck.svg", null, SupplierListPanel.class, null),
    SUPPLIER_CATEGORY("Supplier Category", "", SUPPLIER, SupplierCategoryListPanel.class, null),
    ORDER("Order", "FileTextFill.svg", null, OrderListPanel.class, null),
    INVOICE("Invoice", "Coin.svg", null, InvoiceListPanel.class, null),
    USER("User", "PersonFill.svg", null, UserPanel.class, "SUPER_ADMIN"),
    USER_PROFILE("User Profile", "", USER, UserProfilePanel.class, null),
    ROLE("Role", "", USER, RoleListPanel.class, "SUPER_ADMIN"),
    PERMISSION("Permission", "", USER, PermissionListPanel.class, "SUPER_ADMIN"),
    CUSTOMER("Customer", "PeopleFill.svg", null, CustomerListPanel.class, null),
    SETTING("Setting", "MaterialSymbolsSettings.svg", null, SettingPanel.class, null),
    POINT_OF_SALE("Point Of Sale", "", null, PointOfSalePanel.class, null),
    CART("Cart", "", POINT_OF_SALE, CartPanel.class, null);

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
package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.page.customer.CustomerListPanel;
import com.nhom4.nhtsstore.ui.page.invoice.InvoiceListPanel;
import com.nhom4.nhtsstore.ui.page.order.OrderListPanel;
import com.nhom4.nhtsstore.ui.page.productCategory.ProductCategoryListPanel;
import com.nhom4.nhtsstore.ui.page.supplier.SupplierListPanel;
import com.nhom4.nhtsstore.ui.page.supplierCategory.SupplierCategoryListPanel;
import com.nhom4.nhtsstore.ui.page.user.UserListPanel;
import com.nhom4.nhtsstore.ui.pointOfSale.CartPanel;
import com.nhom4.nhtsstore.ui.pointOfSale.PointOfSalePanel;
import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import com.nhom4.nhtsstore.ui.page.permission.PermissionListPanel;
import com.nhom4.nhtsstore.ui.page.role.RoleListPanel;
import com.nhom4.nhtsstore.ui.page.setting.SettingPanel;
import com.nhom4.nhtsstore.ui.page.user.UserProfilePanel;
import com.nhom4.nhtsstore.ui.page.product.ProductListPanel;
import lombok.Getter;
import javax.swing.*;
import java.util.Set;

@Getter
public enum AppView {
    DASHBOARD("Dashboard", "Speedometer2.svg", null, DashBoardPanel.class, Set.of("SUPER_ADMIN", "MANAGER")),
    PRODUCT("Product", "BoxSeamFill.svg", null, ProductListPanel.class, Set.of("SUPER_ADMIN", "MANAGER")),
    CATEGORY("Category", "", PRODUCT, ProductCategoryListPanel.class, Set.of("SUPER_ADMIN")),
    SUPPLIER("Supplier", "Truck.svg", null, SupplierListPanel.class, Set.of("SUPER_ADMIN", "MANAGER")),
    SUPPLIER_CATEGORY("Supplier Category", "", SUPPLIER, SupplierCategoryListPanel.class, Set.of("SUPER_ADMIN", "MANAGER")),
    ORDER("Order", "FileTextFill.svg", null, OrderListPanel.class, Set.of("SUPER_ADMIN", "MANAGER", "SALE")),
    INVOICE("Invoice", "Coin.svg", null, InvoiceListPanel.class, Set.of("SUPER_ADMIN", "MANAGER")),
    USER("User", "PersonFill.svg", null, UserListPanel.class, Set.of("SUPER_ADMIN")),
    USER_PROFILE("User Profile", "", USER, UserProfilePanel.class, Set.of()),
    ROLE("Role", "", USER, RoleListPanel.class, Set.of("SUPER_ADMIN")),
    PERMISSION("Permission", "", USER, PermissionListPanel.class, Set.of("SUPER_ADMIN")),
    CUSTOMER("Customer", "user.png", USER, CustomerListPanel.class, Set.of("SUPER_ADMIN")),
    POINT_OF_SALE("Point Of Sale", "", null, PointOfSalePanel.class, Set.of("SUPER_ADMIN", "MANAGER", "SALE")),
    CART("Cart", "", POINT_OF_SALE, CartPanel.class, Set.of("SUPER_ADMIN", "MANAGER", "SALE")),
    SETTING("Setting", "MaterialSymbolsSettings.svg", null, SettingPanel.class, Set.of("SUPER_ADMIN"));
    private final String name;
    private final String icon;
    private final AppView parent; // for submenus
    private final Class<? extends JPanel> panelClass;
    private final Set<String> requiredRoles; // The roles required to access this view

    AppView(String name, String icon, AppView parent, Class<? extends JPanel> panelClass, Set<String> requiredRoles) {
        this.name = name;
        this.icon = icon;
        this.parent = parent;
        this.panelClass = panelClass;
        this.requiredRoles = requiredRoles;
    }


    public boolean isAccessibleByRole(String role) {
        return requiredRoles.isEmpty() || requiredRoles.contains(role);
    }
}
package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.ui.page.dashboard.DashBoardPanel;
import com.nhom4.nhtsstore.ui.page.login.LoginPanel;
import lombok.Getter;

import javax.swing.*;

@Getter
public enum ViewName {
    LOGIN_VIEW("login","", LoginPanel.class),
    DASHBOARD_VIEW("dashboard","", DashBoardPanel.class),
    PRODUCT_VIEW("product","category",Form_Home.class),
    SUPPLIER_VIEW("supplier","", Form_1.class),
    USER_VIEW("user","user", Form_2.class),
    CUSTOMER_VIEW("customer","user",null),
    SETTING_VIEW("setting","hammer",null);

    private final String name;
    private final String icon;
    private final Class<? extends JPanel> panelClass;
    ViewName(String name, String icon, Class<? extends JPanel> panelClass) {
        this.name = name;
        this.icon = icon;
        this.panelClass = panelClass;
    }


}

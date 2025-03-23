package com.nhom4.nhtsstore.ui;

import lombok.Getter;

@Getter
public enum ViewName {
    LOGIN_VIEW("login"),
    DASHBOARD_VIEW("dashboard"),
    PRODUCT_VIEW("product"),
    SUPPLIER_VIEW("supplier"),
    USER_VIEW("user");

    private final String value;

    ViewName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

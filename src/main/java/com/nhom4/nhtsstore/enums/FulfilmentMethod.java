package com.nhom4.nhtsstore.enums;

import lombok.Getter;

@Getter
public enum FulfilmentMethod {
    CUSTOMER_TAKEAWAY("Khách mang về"),
    DELIVERY("Giao tận nơi");

    private final String displayName;

    FulfilmentMethod(String displayName) {
        this.displayName = displayName;
    }
}
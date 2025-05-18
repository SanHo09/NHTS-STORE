package com.nhom4.nhtsstore.enums;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    ON_DELIVERY("On Delivery"),
    CANCELLED("Cancelled"),;

    private final String displayName;

    DeliveryStatus(String displayName) {
        this.displayName = displayName;
    }
}

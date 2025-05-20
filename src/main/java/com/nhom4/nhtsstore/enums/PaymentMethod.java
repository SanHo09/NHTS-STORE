package com.nhom4.nhtsstore.enums;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    ZALOPAY("ZaloPay");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 
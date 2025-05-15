package com.nhom4.nhtsstore.enums;

public enum PaymentStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
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
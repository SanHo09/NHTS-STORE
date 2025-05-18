package com.nhom4.nhtsstore.services.payment;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface PaymentStrategy {
    boolean processPayment(Order order, BigDecimal amount);
    PaymentStatus checkPaymentStatus(String transactionId);
    Optional<String> getQRCodeUrl(Order order);
    String getQRCodeFieldName(); // Returns field name like "order_url" or "qrCodeUrl" based on the payment method
    Map<String, Object> getPaymentDetails(String transactionId);

}
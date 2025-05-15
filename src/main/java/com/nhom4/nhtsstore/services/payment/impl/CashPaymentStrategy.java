package com.nhom4.nhtsstore.services.payment.impl;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.services.payment.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Order order, BigDecimal amount) {
        // For cash payments, we can mark them as completed immediately
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setPaymentTransactionId("CASH-" + order.getId() + "-" + System.currentTimeMillis());
        return true;
    }

    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
        // Cash payments are always completed once processed
        return PaymentStatus.COMPLETED;
    }

    @Override
    public Optional<String> getQRCodeUrl(Order order) {
        return Optional.empty();
    }

    @Override
    public String getQRCodeFieldName() {
        return "";
    }

    @Override
    public Map<String, Object> getPaymentDetails(String transactionId) {
        return Map.of();
    }
}
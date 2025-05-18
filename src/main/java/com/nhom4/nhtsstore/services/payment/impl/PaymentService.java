package com.nhom4.nhtsstore.services.payment.impl;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.services.IPaymentService;
import com.nhom4.nhtsstore.services.payment.PaymentStrategy;
import com.nhom4.nhtsstore.services.payment.PaymentStrategyFactory;
import com.nhom4.nhtsstore.services.payment.impl.zalopay.ZaloPayPaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class PaymentService implements IPaymentService {
    private final PaymentStrategyFactory strategyFactory;

    @Autowired
    public PaymentService(PaymentStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    @Override
    public boolean processPayment(Order order) {
        if (order.getPaymentMethod() == null) {
            throw new IllegalStateException("Payment method not specified");
        }

        PaymentStrategy strategy = strategyFactory.getStrategy(order.getPaymentMethod());
        return strategy.processPayment(order, order.getTotalAmount());
    }

    @Override
    public PaymentStatus checkPaymentStatus(Order order) {
        if (order == null || order.getPaymentStatus() == null || order.getPaymentTransactionId() == null) {
            return PaymentStatus.FAILED;
        }

        PaymentStrategy strategy = strategyFactory.getStrategy(order.getPaymentMethod());
        return strategy.checkPaymentStatus(order.getPaymentTransactionId());
    }

    @Override
    public Map<String, Object> getPaymentData(Order order) {
        if (order == null || order.getPaymentTransactionId() == null) {
            return Collections.emptyMap();
        }

        PaymentStrategy strategy = strategyFactory.getStrategy(order.getPaymentMethod());
        if (strategy instanceof ZaloPayPaymentStrategy) {
            return ((ZaloPayPaymentStrategy) strategy).getPaymentDetails(order.getPaymentTransactionId());
        }

        return Collections.emptyMap();
    }
}
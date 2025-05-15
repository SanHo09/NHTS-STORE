package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentStatus;

import java.util.Map;

public interface IPaymentService {
    boolean processPayment(Order order);
    PaymentStatus checkPaymentStatus(Order order);
    Map<String, Object> getPaymentData(Order order);
}
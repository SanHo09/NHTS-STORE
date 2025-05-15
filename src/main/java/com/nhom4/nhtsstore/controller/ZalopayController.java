package com.nhom4.nhtsstore.controller;

import com.nhom4.nhtsstore.services.payment.impl.zalopay.ZaloPayPaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/zalopay")
public class ZalopayController {

    @Autowired
    private ZaloPayPaymentStrategy zaloPayStrategy;

    @PostMapping
    public ResponseEntity<String> createPayment(@RequestBody Map<String, Object> orderRequest) {
        try {
            String response = zaloPayStrategy.createOrder(orderRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/order-status/{appTransId}")
    public ResponseEntity<String> getOrderStatus(@PathVariable String appTransId) {
        String response = zaloPayStrategy.getOrderStatus(appTransId);
        return ResponseEntity.ok(response);
    }

}

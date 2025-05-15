package com.nhom4.nhtsstore.services.payment;

import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.services.payment.impl.CashPaymentStrategy;
import com.nhom4.nhtsstore.services.payment.impl.zalopay.ZaloPayPaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentStrategyFactory {
    private final ZaloPayPaymentStrategy zaloPayStrategy;
    private final CashPaymentStrategy cashStrategy;

    @Autowired
    public PaymentStrategyFactory(
            ZaloPayPaymentStrategy zaloPayStrategy,
            CashPaymentStrategy cashStrategy
//            MoMoPaymentStrategy momoStrategy
    ) {
        this.zaloPayStrategy = zaloPayStrategy;
        this.cashStrategy = cashStrategy;
//        this.momoStrategy = momoStrategy;
    }

    public PaymentStrategy getStrategy(PaymentMethod method) {
        return switch (method) {
            case ZALOPAY -> zaloPayStrategy;
//            case MOMO:
//                return momoStrategy;
            default -> cashStrategy;
        };
    }
}

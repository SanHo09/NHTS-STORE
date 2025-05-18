package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table
@Getter
@Setter
public class OrderDetail {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = true)
    private int quantity = 0;

    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal unitPrice=BigDecimal.ZERO;

    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal unitCost=BigDecimal.ZERO;
    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal subtotal=BigDecimal.ZERO;





}

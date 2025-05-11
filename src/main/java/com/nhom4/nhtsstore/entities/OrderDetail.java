package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}

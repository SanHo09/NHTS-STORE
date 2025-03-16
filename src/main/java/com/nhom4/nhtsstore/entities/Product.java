package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table
@Getter
@Setter
public class Product {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String Name;

    @Column(nullable = false)
    private double SalePrice;

    @Column(nullable = true)
    private double PurchasePrice;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date ManufactureDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date ExpiryDate;

    @Column(nullable = true)
    private String Manufacturer;

    @Column(nullable = true)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier Supplier;
}

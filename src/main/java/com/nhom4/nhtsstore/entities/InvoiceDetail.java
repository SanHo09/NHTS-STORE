package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class InvoiceDetail {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice Invoice;

    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = false)
    private Product Product;
}

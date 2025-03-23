package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Invoice {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date CreateDate;

    @Column(nullable = false)
    private double TotalAmount;

    @OneToMany(mappedBy = "Invoice")
    private List<InvoiceDetail> InvoiceDetail;
}

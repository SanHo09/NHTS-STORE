package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Invoice extends GenericEntity {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private double totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetail;

    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public Object getFieldValueByIndex(int index) {
        java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat decimalFormatter = new DecimalFormat("###,###,###");
        switch (index) {
            case 0: return id;
            case 1: return createDate != null ? dateFormatter.format(createDate) : null;
            case 2: return decimalFormatter.format(totalAmount);
            case 3: return customer.getName() != null ? customer.getName() : null;
            default: return null;
        }
    }
}

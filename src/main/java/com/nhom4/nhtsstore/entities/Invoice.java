package com.nhom4.nhtsstore.entities;

import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.enums.FulfilmentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetail;

    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;
    
    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    @Column
    private String paymentTransactionId;

    @Column
    @Enumerated(EnumType.STRING)
    private FulfilmentMethod fulfilmentMethod;

    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.valueOf(5.0); // Default value for shipping cost

    @Column(nullable = true, name = "delivery_address")
    private String deliveryAddress;

    @Column
    private String phoneNumber;


    
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
            case 3: return createDate != null ? dateFormatter.format(createDate) : null;
            case 1: return decimalFormatter.format(totalAmount);
            case 2: return customer.getName() != null ? customer.getName() : null;
            default: return null;
        }
    }
}

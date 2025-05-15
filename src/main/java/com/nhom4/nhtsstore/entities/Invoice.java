package com.nhom4.nhtsstore.entities;

import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetail;

    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = false)
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
    private String shippingAddress;

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

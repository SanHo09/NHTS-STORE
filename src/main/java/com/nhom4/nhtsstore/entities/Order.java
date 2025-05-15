package com.nhom4.nhtsstore.entities;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.enums.OrderStatus;
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
@Table(name = "orders")
@Getter
@Setter
public class Order extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false,  precision = 19, scale = 2)
    private BigDecimal totalAmount= BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    @Column
    private String paymentTransactionId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
    
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public Object getFieldValueByIndex(int index) {
        DateTimeFormatter auditDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("dd/MM/yyyy");


        switch (index) {
            case 0: return id;
            case 1: return customer.getName();
            case 2: return getLastModifiedOn() != null ? dateFormatter.format(createDate) : null;
            case 3: return totalAmount;
            case 4: return status;
            case 5: return paymentMethod;
            case 6: return paymentStatus;
            case 7: return lastModifiedOn != null ? lastModifiedOn.format(auditDateFormatter) : null;
            case 8: return lastModifiedBy;
            default: return null;
        }
    }
}

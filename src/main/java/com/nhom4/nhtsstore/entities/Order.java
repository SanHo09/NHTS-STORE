package com.nhom4.nhtsstore.entities;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.enums.DeliveryStatus;
import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.enums.FulfilmentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @Column(nullable = true,name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.valueOf(5.0); // Default value for shipping cost

    @Column
    @Enumerated(EnumType.STRING)
    private FulfilmentMethod fulfilmentMethod;

    @Column(nullable = true,name = "delivery_address")
    private String deliveryAddress;
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
            case 4: return fulfilmentMethod;
            case 5: return deliveryStatus;
            case 6: return paymentMethod;
            case 7: return paymentStatus;
            case 8: return lastModifiedOn != null ? lastModifiedOn.format(auditDateFormatter) : null;
            case 9: return lastModifiedBy;
            default: return null;
        }
    }
}

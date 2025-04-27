package com.nhom4.nhtsstore.entities;

import com.nhom4.nhtsstore.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends GenericEntity {
    @Id
    @Column
    @GenericGenerator(name="autoGenerate" , strategy="increment")
    private Long Id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date CreateDate;

    @Column(nullable = false)
    private double TotalAmount;

    @Column(nullable = false)
    private OrderStatus Status;

    @OneToMany(mappedBy = "Order")
    private List<OrderDetail> OrderDetails;

    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer Customer;
}

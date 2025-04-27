package com.nhom4.nhtsstore.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Customer extends GenericEntity {
    @Id
    @Column
    @GenericGenerator(name="autoGenerate" , strategy="increment")
    private Long Id;

    @Column(nullable = false)
    @Nationalized
    private String Name;

    @Column(nullable = false)
    @Email
    private String Email;

    @Column(nullable = false)
    private String PhoneNumber;

    @Column(nullable = false)
    private String Address;

    @OneToMany(mappedBy = "Customer")
    private List<Order> Order;
}

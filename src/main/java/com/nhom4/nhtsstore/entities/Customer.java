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
    private Long id;

    @Column(nullable = false)
    @Nationalized
    private String name;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "customer")
    private List<Order> order;
}

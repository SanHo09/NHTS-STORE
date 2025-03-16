package com.nhom4.nhtsstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Supplier {
    @Id
    @GenericGenerator(name="autoGenerate" , strategy="increment")
    @GeneratedValue(generator="autoGenerate")
    private Long Id;

    @Column(nullable = false)
    private String Name;

    @Column(nullable = false)
    private String Address;

    @JsonIgnore
    @OneToMany(mappedBy = "Supplier")
    private List<Product> products;
}

package com.nhom4.nhtsstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Category {
    @Id
    @Column
    @GenericGenerator(name="autoGenerate" , strategy="increment")
    private Long Id;

    @Column(nullable = false)
    @Nationalized
    private String Name;

    @JsonIgnore
    @OneToMany(mappedBy = "Category")
    private List<Product> Products;
}

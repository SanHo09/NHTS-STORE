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
public class Supplier extends GenericEntity {
    @Id
    @GenericGenerator(name="autoGenerate" , strategy="increment")
    @GeneratedValue(generator="autoGenerate")
    private Long id;

    @Column(nullable = false)
    @Nationalized
    private String name;

    @Column(nullable = false)
    @Nationalized
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier")
    private List<Product> products;
    
    @Override
    public String toString() {
        return this.name;
    }
        
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return id != null && id.equals(supplier.id);
    }
}

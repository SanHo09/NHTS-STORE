package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends GenericEntity {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Nationalized
    private String name;

    @Column(nullable = false)
    private double salePrice;

    @Column(nullable = true)
    private double purchasePrice;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date manufactureDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date expiryDate;

    @Column(nullable = true)
    @Nationalized
    private String manufacturer;

    @Column(nullable = true)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public Object getFieldValueByIndex(int index) {
        switch (index) {
            case 0: return name;
            case 1: return salePrice;
            case 2: return category.getName();
            case 3: return quantity;
            case 4: return expiryDate;
            case 5: return isActive() ? "Active" : "Inactive";
            default: return null;
        }
    }
}

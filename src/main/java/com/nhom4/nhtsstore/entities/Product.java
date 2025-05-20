package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Nationalized;

import java.util.Date;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@Builder
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

    @Column(nullable = false,precision = 19, scale = 2)
    private BigDecimal salePrice= BigDecimal.ZERO;

    @Column(nullable = true,precision = 19, scale = 2)
    private BigDecimal purchasePrice= BigDecimal.ZERO;

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

    @Column(nullable = true, unique = true)
    private String barcode;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProductImage> images = new ArrayList<>();

    public ProductImage getThumbnail() {
        return images.stream()
                .filter(ProductImage::isThumbnail)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Object getFieldValueByIndex(int index) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        switch (index) {
            case 0: return id;
            case 1: return name;
            case 2: return salePrice;
            case 3: return category.getName();
            case 4: return quantity > 0 ? "In stock" : "Out of stock";
            case 5: return expiryDate;
            case 6: return isActive() ? "Visible" : "Hidden";
            case 7: return lastModifiedOn != null ? lastModifiedOn.format(formatter) : null;
            case 8: return lastModifiedBy;
            default: return null;
        }
    }
}

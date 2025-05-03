package com.nhom4.nhtsstore.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage extends GenericEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Lob
    @Column(nullable = false)
    private byte[] imageData;
    
    @Column(nullable = false)
    private String imageName;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private boolean isThumbnail;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Override
    public Long getId() {
        return id;
    }
}
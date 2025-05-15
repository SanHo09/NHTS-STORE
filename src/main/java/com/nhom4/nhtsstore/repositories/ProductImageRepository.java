package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    void deleteByProductId(Long productId);
    List<ProductImage> findProductImagesByProduct_Id(Long productId);
}

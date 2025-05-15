package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.ProductImage;

import java.util.List;

public interface IProductImageService {
    void deleteAll(List<ProductImage> images);
    ProductImage save(ProductImage entity);
    List<ProductImage> findByProductId(Long productId);
}

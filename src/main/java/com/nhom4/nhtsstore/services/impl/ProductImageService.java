package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.ProductImage;
import com.nhom4.nhtsstore.repositories.ProductImageRepository;
import java.util.List;

import com.nhom4.nhtsstore.services.IProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService implements IProductImageService {
    @Autowired
    private ProductImageRepository repository;
    @Override
    public void deleteAll(List<ProductImage> images) {
        repository.deleteAll(images);
    }
    @Override
    public ProductImage save(ProductImage entity) {
        return repository.save(entity);
    }

    @Override
    public List<ProductImage> findByProductId(Long productId) {
        return repository.findProductImagesByProduct_Id(productId);
    }
}

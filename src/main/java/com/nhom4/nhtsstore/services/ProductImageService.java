package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.ProductImage;
import com.nhom4.nhtsstore.repositories.ProductImageRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository repository;
    
    public void deleteAll(List<ProductImage> images) {
        repository.deleteAll(images);
    }
    
    public ProductImage save(ProductImage entity) {
        return repository.save(entity);
    }
}

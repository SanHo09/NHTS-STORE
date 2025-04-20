package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 *
 * @author NamDang
 */
@Service
public class ProductService implements GenericService<Product> {
    
    @Autowired
    private ProductRepository repository;
    
    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Product findById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    @Override
    public Product save(Product entity) {
        return repository.save(entity);
    }
    
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteMany(List<Product> entities) {
        repository.deleteAll(entities);
    }
}

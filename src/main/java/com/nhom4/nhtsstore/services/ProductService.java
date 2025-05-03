package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.repositories.ProductImageRepository;
import com.nhom4.nhtsstore.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author NamDang
 */
@Service
public class ProductService implements GenericService<Product> {
    
    @Autowired
    private ProductRepository repository;
        
    @Autowired
    private ProductImageRepository productImageRepository;
    
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
        productImageRepository.deleteByProductId(id);
        repository.deleteById(id);
    }

    @Override
    public void deleteMany(List<Product> entities) {
        List<Long> productIds = entities.stream()
                                    .map(Product::getId)
                                    .collect(Collectors.toList());
        for (Long productId : productIds) {
            productImageRepository.deleteByProductId(productId);
        }
        repository.deleteAll(entities);
    }
    
    @Override
    public Page<Product> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }
    
    @Override
    public Page<Product> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Product> keywordSpec = Specification.where(null);
            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) -> 
                    cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%"));
            }
            spec = spec.and(keywordSpec);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(spec, pageable);
    }
}

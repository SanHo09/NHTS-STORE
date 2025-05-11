package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class SupplierService implements ISupplierService, GenericService<Supplier> {
    @Autowired
    private SupplierRepository repository;

    @Override
    public List<Supplier> getSupplier() {
        // Add more logic
        return repository.findAll();
    }

    @Override
    public List<Supplier> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Supplier findById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    @Override
    public Supplier save(Supplier entity) {
        return repository.save(entity);
    }
    
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteMany(List<Supplier> entities) {
        repository.deleteAll(entities);
    }
    
    @Override
    public Page<Supplier> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }
    
    @Override
    public Page<Supplier> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Supplier> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Supplier> keywordSpec = Specification.where(null);
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

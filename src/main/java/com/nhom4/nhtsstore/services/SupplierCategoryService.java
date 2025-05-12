package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.SupplierCategory;
import com.nhom4.nhtsstore.repositories.SupplierCategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierCategoryService implements GenericService<SupplierCategory> {
   @Autowired
   private SupplierCategoryRepository repository;
   
   @Override
   public List<SupplierCategory> findAll() {
       return repository.findAll();
   }
   
   @Override
   public SupplierCategory findById(Long id) {
       return repository.findById(id).orElse(null);
   }
   
   @Override
   public SupplierCategory save(SupplierCategory entity) {
       return repository.save(entity);
   }
   
    @Override
    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete. This category is used by some suppliers!");
        }
    }

    @Override
    public void deleteMany(List<SupplierCategory> entities) {
        try {
            repository.deleteAll(entities);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete. Categories are used by some products!");
        }
    }
   
   @Override
   public Page<SupplierCategory> findAll(Pageable pageable) {
       pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
       return repository.findAll(pageable);
   }
   
   @Override
   public Page<SupplierCategory> search(String keyword, List<String> searchFields, Pageable pageable) {
       Specification<SupplierCategory> spec = Specification.where(null);
       if (keyword != null && !keyword.isEmpty() && searchFields != null) {
           Specification<SupplierCategory> keywordSpec = Specification.where(null);
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

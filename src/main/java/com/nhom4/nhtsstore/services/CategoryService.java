package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.repositories.CategoryRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author NamDang
 */
@Service
public class CategoryService implements GenericService<Category> {
    
    @Autowired
    private CategoryRepository repository;
    
    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Category findById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    @Override
    public Category save(Category entity) {
        return repository.save(entity);
    }
    
    @Override
    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete. This category is used by some products!");
        }
    }

    @Override
    public void deleteMany(List<Category> entities) {
        try {
            repository.deleteAll(entities);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete. Categories are used by some products!");
        }
    }
    
    @Override
    public Page<Category> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }
    
    @Override
    public Page<Category> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Category> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Category> keywordSpec = Specification.where(null);
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

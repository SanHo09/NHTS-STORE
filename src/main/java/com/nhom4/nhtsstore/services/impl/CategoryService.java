package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.repositories.CategoryRepository;
import java.util.List;

import com.nhom4.nhtsstore.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * @author NamDang
 */
@Service
public class CategoryService implements ICategoryService {
    @Override
    public CategoryRepository getRepository() {
        return repository;
    }

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
    

}

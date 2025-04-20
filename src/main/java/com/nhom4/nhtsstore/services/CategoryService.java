//package com.nhom4.nhtsstore.services;
//
//import com.nhom4.nhtsstore.entities.Category;
//import com.nhom4.nhtsstore.repositories.CategoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
///**
// *
// * @author NamDang
// */
//@Service
//public class CategoryService implements GenericService<Category> {
//    
//    @Autowired
//    private CategoryRepository repository;
//    
//    @Override
//    public List<Category> findAll() {
//        return repository.findAll();
//    }
//    
//    @Override
//    public Category findById(Long id) {
//        return repository.findById(id).orElse(null);
//    }
//    
//    @Override
//    public Category save(Category entity) {
//        return repository.save(entity);
//    }
//    
//    @Override
//    public void deleteById(Long id) {
//        repository.deleteById(id);
//    }
//
//    @Override
//    public void deleteMany(List<Category> entities) {
//        repository.deleteAll(entities);
//    }
//}

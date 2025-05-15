package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author NamDang
 */
public interface CategoryRepository extends GenericRepository<Category, Long>{
    
}

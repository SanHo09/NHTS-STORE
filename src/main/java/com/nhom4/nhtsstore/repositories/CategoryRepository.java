package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author NamDang
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}

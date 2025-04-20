package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author NamDang
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}

package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * @author NamDang
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p.category.name, p.active, SUM(p.quantity) FROM Product p " +
            "GROUP BY p.category.name, p.active")
    List<Object[]> getInventoryByCategoryAndStatus();

    @Query("SELECT p.name, p.salePrice, p.purchasePrice, (p.salePrice - p.purchasePrice) as profit " +
            "FROM Product p WHERE p.active = true " +
            "ORDER BY (p.salePrice - p.purchasePrice) DESC")
    List<Object[]> getProductProfitability(Pageable pageable);
}

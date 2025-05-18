package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 *
 * @author NamDang
 */
public interface ProductRepository extends GenericRepository<Product,Long> {
    @Query("SELECT p.category.name, p.active, SUM(p.quantity) FROM Product p " +
            "GROUP BY p.category.name, p.active")
    List<Object[]> getInventoryByCategoryAndStatus();

    @Query("SELECT p.name, " +
            "SUM(od.quantity * (od.unitPrice - od.unitCost)) as profit " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.deliveryStatus = 'COMPLETED' " +
            "GROUP BY p.name " +
            "ORDER BY profit DESC")
    List<Object[]> getProductProfitability(Pageable pageable);

    Product findByBarcodeAndActiveIsTrue(String barcode);
    Page<Product> findAllByActiveIsTrue(Pageable pageable);

}

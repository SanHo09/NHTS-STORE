package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService extends GenericService<Product, Long, ProductRepository> {
    Product findByBarcode(String barcode);
    Page<Product> searchWhereIsActive(String keyword, List<String> searchFields, Pageable pageable);
    Page<Product> findAllByActiveIsTrue(Pageable pageable);

}

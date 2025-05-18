package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.repositories.ProductImageRepository;
import com.nhom4.nhtsstore.repositories.ProductRepository;
import com.nhom4.nhtsstore.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author NamDang
 */
@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Product save(Product entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        productImageRepository.deleteByProductId(id);
        repository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<Product> entities) {
        List<Long> productIds = entities.stream()
                                    .map(Product::getId)
                                    .collect(Collectors.toList());
        for (Long productId : productIds) {
            productImageRepository.deleteByProductId(productId);
            repository.deleteById(productId);
        }
//        repository.deleteAll(entities);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }

    @Override
    public ProductRepository getRepository() {
        return this.repository;
    }

    @Override
    public Page<Product> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Product> keywordSpec = Specification.where(null);
            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) -> 
                    cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%"));
            }
            spec = spec.and(keywordSpec);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(spec, pageable);
    }

    @Override
    public Product findByBarcode(String barcode) {
        return repository.findByBarcodeAndActiveIsTrue(barcode);
    }

    @Override
    public Page<Product> searchWhereIsActive(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Product> keywordSpec = Specification.where(null);
            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) ->
                        cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%"))
                        .and((root, query, cb) -> cb.equal(root.get("active"), true));
            }
            spec = spec.and(keywordSpec);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(spec, pageable);
    }

    @Override
    public Page<Product> findAllByActiveIsTrue(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAllByActiveIsTrue(pageable);
    }
}

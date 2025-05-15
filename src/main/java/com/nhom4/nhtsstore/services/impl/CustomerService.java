package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.repositories.CustomerRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.nhom4.nhtsstore.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService implements ICustomerService {
    @Override
    public CustomerRepository getRepository() {
        return repository;
    }

    @Autowired
    private CustomerRepository repository;

    @Override
    public Customer findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Customer findByEmailOrPhoneNumber(String email, String phoneNumber) {
        return repository.findByEmailOrPhoneNumber(email, phoneNumber);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Customer save(Customer entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<Customer> entities) {
        List<Long> customerIds = entities.stream()
                                    .map(Customer::getId)
                                    .collect(Collectors.toList());
        for (Long customerId : customerIds) {
            repository.deleteById(customerId);
        }
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }

    @Override
    public Page<Customer> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Customer> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Customer> keywordSpec = Specification.where(null);
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

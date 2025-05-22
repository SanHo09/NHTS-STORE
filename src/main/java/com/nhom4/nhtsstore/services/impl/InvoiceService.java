package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.repositories.InvoiceRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.nhom4.nhtsstore.services.IInvoiceService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService implements IInvoiceService {
    @Override
    public InvoiceRepository getRepository() {
        return this.repository;
    }

    @Autowired
    private InvoiceRepository repository;
    
    @Autowired
    private ApplicationState applicationState;
    
    @Override
    public List<Invoice> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Invoice findById(Long id) {
        String role = applicationState.getCurrentUser().getRole();
        String username = applicationState.getCurrentUser().getUsername();
        
        Invoice invoice = null;
        
        if (role.equals("SALE")) {
            invoice = repository.findByIdAndCreatedBy(id, username).orElse(null);
        } else {
            invoice = repository.findById(id).orElse(null);
        }
        return invoice;
    }
    
    @Override
    public Invoice save(Invoice entity) {
        return repository.save(entity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<Invoice> entities) {
        List<Long> invoiceIds = entities.stream()
                                    .map(Invoice::getId)
                                    .collect(Collectors.toList());
        for (Long invoiceId : invoiceIds) {
            repository.deleteById(invoiceId);
        }

    }
    
    @Override
    public Page<Invoice> findAll(Pageable pageable) {
        String role = applicationState.getCurrentUser().getRole();
        String username = applicationState.getCurrentUser().getUsername();
        
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        Page<Invoice> result;
        
        if (role.equals("SALE")) {
            result = repository.findAllByCreatedBy(username, pageable);
        } else {
            result = repository.findAll(pageable);
        }
        return result;
    }
    
    @Override
    public Page<Invoice> search(String keyword, List<String> searchFields, Pageable pageable) {
        String role = applicationState.getCurrentUser().getRole();
        String username = applicationState.getCurrentUser().getUsername();
        
        Specification<Invoice> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Invoice> keywordSpec = Specification.where(null);

            for (String field : searchFields) {
                keywordSpec = keywordSpec.or((root, query, cb) -> {
                    if (field.equals("id")) {
                        try {
                            // Convert the Long id to a string and apply LIKE search
                            String keywordStr = keyword.toLowerCase();
                            return cb.like(cb.lower(cb.toString(root.get("id"))), "%" + keywordStr + "%");
                        } catch (NumberFormatException e) {
                            // Skip this predicate if keyword is not a valid Long
                            return cb.disjunction();
                        }
                    } else {
                        return cb.like(cb.lower(root.get(field)), "%" + keyword.toLowerCase() + "%");
                    }
                });
            }

            spec = spec.and(keywordSpec);
        }
        if (role.equals("SALE")) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("createdBy"), username));
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        return repository.findAll(spec, pageable);
    }

}

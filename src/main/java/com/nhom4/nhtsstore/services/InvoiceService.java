package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.repositories.InvoiceRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService implements GenericService<Invoice> {
    @Autowired
    private InvoiceRepository repository;
    
    @Override
    public List<Invoice> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Invoice findById(Long id) {
        return repository.findById(id).orElse(null);
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
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        return repository.findAll(pageable);
    }
    
    @Override
    public Page<Invoice> search(String keyword, List<String> searchFields, Pageable pageable) {
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

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        return repository.findAll(spec, pageable);
    }

}

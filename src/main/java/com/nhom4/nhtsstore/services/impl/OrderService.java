package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.repositories.OrderDetailRepository;
import com.nhom4.nhtsstore.repositories.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.nhom4.nhtsstore.services.IOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService implements IOrderService {
    
    @Autowired
    private OrderRepository repository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Order findById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    @Override
    public Order save(Order entity) {
        return repository.saveAndFlush(entity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<Order> entities) {
        List<Long> orderIds = entities.stream()
                                    .map(Order::getId)
                                    .collect(Collectors.toList());
        for (Long orderId : orderIds) {
            repository.deleteById(orderId);
        }

    }
    
    @Override
    public Page<Order> findAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        return repository.findAll(pageable);
    }

    @Override
    public OrderRepository getRepository() {
        return this.repository;
    }

    @Override
    public Page<Order> search(String keyword, List<String> searchFields, Pageable pageable) {
        Specification<Order> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Order> keywordSpec = Specification.where(null);

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

    
    @Override
    public Order findByUserId(Long userId) {
        List<Order> orders = repository.findByUserId(userId);
        return orders.isEmpty() ? null : orders.getFirst();
    }

    @Override
    @Transactional
    public void removeProductFromOrderByProductId(Long userId, Long productId) {
        Order order = repository.findByUserId(userId).stream().findFirst().orElse(null);
        if (order == null) return;

        OrderDetail toRemove = order.getOrderDetails().stream()
                .filter(od -> od.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            order.getOrderDetails().remove(toRemove);
            // If orphanRemoval = true, JPA will delete the detail automatically
            repository.save(order);
        }
    }

    @Override
    public void remove(long id) {
        repository.deleteById(id);
    }


}

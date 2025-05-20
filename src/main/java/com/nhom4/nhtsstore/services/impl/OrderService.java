package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.repositories.OrderDetailRepository;
import com.nhom4.nhtsstore.repositories.OrderRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.nhom4.nhtsstore.services.IOrderService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private ApplicationState applicationState;

    @Override
    public List<Order> findAll() {
        log.debug("Finding all orders");
        List<Order> orders = repository.findAll();
        log.debug("Found {} orders", orders.size());
        return orders;
    }

    @Override
    public Order findById(Long id) {
        String role = applicationState.getCurrentUser().getRole();
        String username = applicationState.getCurrentUser().getUsername();
        
        log.debug("Finding order with id: {}", id);

        Order order = null;
        if (role.equals("SALE")) {
            order = repository.findByIdAndCreatedBy(id, username).orElse(null);
        } else {
            order = repository.findById(id).orElse(null);
        }
        if (order != null) {
            log.debug("Found order with id: {}, total items: {}", id, order.getOrderDetails().size());
        } else {
            log.debug("Order with id {} not found", id);
        }
        return order;
    }

    @Override
    public Order save(Order entity) {
        if (entity.getId() == null) {
            log.info("Creating new order for user id: {}", entity.getUser().getId());
            entity.setCreateDate(new Date());
        } else {
            log.info("Updating order with id: {}", entity.getId());
        }
        Order savedOrder = repository.saveAndFlush(entity);
        log.debug("Order saved successfully with id: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        log.info("Deleting order with id: {}", id);
        try {
            repository.deleteById(id);
            log.info("Successfully deleted order with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting order with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<Order> entities) {
        List<Long> orderIds = entities.stream()
                                    .map(Order::getId)
                                    .collect(Collectors.toList());
        log.info("Deleting {} orders with ids: {}", orderIds.size(), orderIds);
        try {
            for (Long orderId : orderIds) {
                repository.deleteById(orderId);
                log.debug("Deleted order with id: {}", orderId);
            }
            log.info("Successfully deleted {} orders", orderIds.size());
        } catch (Exception e) {
            log.error("Error deleting multiple orders: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        String role = applicationState.getCurrentUser().getRole();
        String username = applicationState.getCurrentUser().getUsername();
        
        log.debug("Finding orders with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedOn").descending());
        Page<Order> result;

        if (role.equals("SALE")) {
            result = repository.findAllByCreatedBy(username, pageable);
        } else {
            result = repository.findAll(pageable);
        }
        log.debug("Found {} orders (page {} of {})", result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages());
        return result;
    }

    @Override
    public OrderRepository getRepository() {
        log.debug("Getting order repository");
        return this.repository;
    }

    @Override
    public Page<Order> search(String keyword, List<String> searchFields, Pageable pageable) {
        String role = applicationState.getCurrentUser().getRole();
        String username = applicationState.getCurrentUser().getUsername();
        
        log.debug("Searching orders with keyword: '{}', fields: {}", keyword, searchFields);
        Specification<Order> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty() && searchFields != null) {
            Specification<Order> keywordSpec = Specification.where(null);

            for (String field : searchFields) {
                log.debug("Adding search criteria for field: {}", field);
                keywordSpec = keywordSpec.or((root, query, cb) -> {
                    if (field.equals("id")) {
                        try {
                            // Convert the Long id to a string and apply LIKE search
                            String keywordStr = keyword.toLowerCase();
                            return cb.like(cb.lower(cb.toString(root.get("id"))), "%" + keywordStr + "%");
                        } catch (NumberFormatException e) {
                            log.debug("Invalid number format for ID search: {}", keyword);
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
        Page<Order> result = repository.findAll(spec, pageable);
        log.debug("Search found {} orders", result.getTotalElements());
        return result;
    }


    @Override
    public Order findByUserId(Long userId) {
        log.debug("Finding order for user id: {}", userId);
        List<Order> orders = repository.findByUserId(userId);
        if (orders.isEmpty()) {
            log.debug("No orders found for user id: {}", userId);
            return null;
        } else {
            log.debug("Found order with id: {} for user id: {}", orders.getFirst().getId(), userId);
            return orders.getFirst();
        }
    }

    @Override
    @Transactional
    public void removeProductFromOrderByProductId(Long userId, Long productId) {
        log.info("Removing product id: {} from order for user id: {}", productId, userId);
        Order order = repository.findByUserId(userId).stream().findFirst().orElse(null);
        if (order == null) {
            log.debug("No order found for user id: {}, cannot remove product", userId);
            return;
        }

        OrderDetail toRemove = order.getOrderDetails().stream()
                .filter(od -> od.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            log.debug("Removing product: {} (quantity: {}) from order id: {}", 
                    toRemove.getProduct().getName(), toRemove.getQuantity(), order.getId());
            order.getOrderDetails().remove(toRemove);
            // If orphanRemoval = true, JPA will delete the detail automatically
            repository.save(order);
            log.info("Successfully removed product id: {} from order id: {}", productId, order.getId());
        } else {
            log.debug("Product id: {} not found in order id: {}", productId, order.getId());
        }
    }

    @Override
    public void remove(long id) {
        log.info("Removing order with id: {}", id);
        try {
            repository.deleteById(id);
            log.info("Successfully removed order with id: {}", id);
        } catch (Exception e) {
            log.error("Error removing order with id {}: {}", id, e.getMessage());
            throw e;
        }
    }


}

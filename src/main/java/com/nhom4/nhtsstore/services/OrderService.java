/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.repositories.OrderDetailRepository;
import com.nhom4.nhtsstore.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Sang
 */
@Service
public class OrderService implements IOrderService{
    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Override
    public Order findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Order save(Order entity) {
        return repository.save(entity);
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.repositories.OrderRepository;

/**
 *
 * @author Sang
 */
public interface IOrderService extends GenericService<Order,Long, OrderRepository>{

    Order findByUserId(Long userId);
    void removeProductFromOrderByProductId(Long userId, Long productId);
    void remove(long id);
}

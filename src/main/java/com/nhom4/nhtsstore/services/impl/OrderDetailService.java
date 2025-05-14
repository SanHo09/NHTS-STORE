package com.nhom4.nhtsstore.services.impl;

import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.repositories.OrderDetailRepository;
import com.nhom4.nhtsstore.services.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService implements IOrderDetailService {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetail save(OrderDetail entity) {
        return orderDetailRepository.save(entity);
    }
}

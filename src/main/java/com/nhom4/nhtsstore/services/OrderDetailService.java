package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.repositories.OrderDetailRepository;
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

package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements ICustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}

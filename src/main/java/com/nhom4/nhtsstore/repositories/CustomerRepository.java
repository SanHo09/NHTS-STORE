package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Customer;

import java.util.List;

public interface CustomerRepository extends GenericRepository<Customer, Long>{
    Customer findByEmail(String email);
    Customer findByPhoneNumber(String phoneNumber);
    Customer findByEmailOrPhoneNumber(String email, String phoneNumber);
    List<Customer> findByEmailOrPhoneNumberOrderByLastModifiedOnDesc(String email, String phoneNumber);
    List<Customer> findByEmailOrderByLastModifiedOnDesc(String email);
    List<Customer> findByPhoneNumberOrderByLastModifiedOnDesc(String phoneNumber);
}

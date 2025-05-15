package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.repositories.CustomerRepository;

public interface ICustomerService extends GenericService<Customer,Long, CustomerRepository>{
    /**
     * Find a customer by email
     * @param email the email to search for
     * @return the customer with the given email, or null if not found
     */
    Customer findByEmail(String email);

    /**
     * Find a customer by phone number
     * @param phoneNumber the phone number to search for
     * @return the customer with the given phone number, or null if not found
     */
    Customer findByPhoneNumber(String phoneNumber);

    /**
     * Find a customer by either email or phone number
     * @param email the email to search for
     * @param phoneNumber the phone number to search for
     * @return the customer with the given email or phone number, or null if not found
     */
    Customer findByEmailOrPhoneNumber(String email, String phoneNumber);
}

package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}

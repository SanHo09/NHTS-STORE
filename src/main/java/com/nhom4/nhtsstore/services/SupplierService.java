package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService implements ISupplierService {
    SupplierService(SupplierRepository SupplierRepository) {
        supplierRepository = SupplierRepository;
    }
    private final SupplierRepository supplierRepository;


    @Override
    public List<Supplier> getSupplier() {
        // Add more logic
        return supplierRepository.findAll();
    }
}

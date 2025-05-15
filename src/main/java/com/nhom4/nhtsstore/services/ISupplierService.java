package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.repositories.SupplierRepository;

import java.util.List;

public interface ISupplierService extends GenericService<Supplier,Long, SupplierRepository> {
    List<Supplier> getSupplier();
}

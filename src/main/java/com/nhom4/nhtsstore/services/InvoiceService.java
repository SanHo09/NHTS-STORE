package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService implements IInvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Invoice save(Invoice entity) {
        return invoiceRepository.save(entity);
    }
}

package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.repositories.SupplierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;
import javax.swing.*;
import lombok.RequiredArgsConstructor;

@Component
public class MainFrame extends JFrame {
    private final MainPanel mainPanel;
    private final SupplierRepository supplierRepository;

    MainFrame(MainPanel mainPanel, SupplierRepository supplierRepository) {
        this.mainPanel = mainPanel;
        this.supplierRepository = supplierRepository;
    }

    @PostConstruct
    private void init() {
        Supplier supplier = new Supplier();
        supplier.setName("Hello World");
        supplier.setAddress("address");

        supplierRepository.save(supplier);
        setTitle("NHTS Store");
        setSize(1200,720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(mainPanel);

        
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom4.nhtsstore.ui.pointOfSale;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.impl.ProductService;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import jakarta.annotation.PostConstruct;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sang
 */
@Component
@Scope("prototype")
public class PointOfSalePanel extends JPanel implements RoutablePanel {

    @Autowired
    private NavigationService navigationService;

    @Autowired
    private ProductService productService;

    private int currentPage = 0;
    private int totalPages = 0;
    private final int pageSize = 9;
    private JButton firstPageBtn, prevPageBtn, nextPageBtn, lastPageBtn;
    private Page<Product> productData;
    private JPanel contentPanel = new JPanel();

    public PointOfSalePanel() {
        setLayout(new BorderLayout());
    }

    @PostConstruct
    public void init() {
        // Set up content panel
        contentPanel.setLayout(new GridLayout(3, 6, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add content to main panel
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        add(contentWrapper, BorderLayout.CENTER);

        // Create top controls
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton barcodeScannerBtn = new JButton("Find Products");
        barcodeScannerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barcodeScannerBtn.addActionListener(e ->
                navigationService.navigateTo(BarcodeScannerPanel.class, new RouteParams()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(barcodeScannerBtn);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Create pagination controls
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        firstPageBtn = new JButton("<<");
        prevPageBtn = new JButton("<");
        nextPageBtn = new JButton(">");
        lastPageBtn = new JButton(">>");

        firstPageBtn.addActionListener(e -> goToPage(0));
        prevPageBtn.addActionListener(e -> goToPage(currentPage - 1));
        nextPageBtn.addActionListener(e -> goToPage(currentPage + 1));
        lastPageBtn.addActionListener(e -> goToPage(totalPages - 1));

        paginationPanel.add(firstPageBtn);
        paginationPanel.add(prevPageBtn);
        paginationPanel.add(nextPageBtn);
        paginationPanel.add(lastPageBtn);
        add(paginationPanel, BorderLayout.SOUTH);

    }


    private void loadData() {
//        SwingWorker<Page<Product>, Void> worker = new SwingWorker<>() {
//            @Override
//            protected Page<Product> doInBackground() {
//                Pageable pageable = PageRequest.of(currentPage, pageSize);
//                return productService.findAllByActiveIsTrue(pageable);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    productData = get();
//                    contentPanel.removeAll();
//
//                    for (Product product : productData) {
//                        ProductItemPanel productItemPanel = new ProductItemPanel(product, navigationService);
//                        contentPanel.add(productItemPanel);
//                    }
//                    totalPages = productData.getTotalPages();
//                    revalidate();
//                    repaint();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(PointOfSalePanel.this, "Failed to load products.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        };
//        worker.execute();
        Thread.startVirtualThread(() -> {
            try {
                Pageable pageable = PageRequest.of(currentPage, pageSize);
                productData = productService.findAllByActiveIsTrue(pageable);
                contentPanel.removeAll();

                for (Product product : productData) {
                    ProductItemPanel productItemPanel = new ProductItemPanel(product, navigationService);
                    contentPanel.add(productItemPanel);
                }
                totalPages = productData.getTotalPages();
                revalidate();
                repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load products.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void goToPage(int page) {
        if (page >= 0 && page < totalPages && page != currentPage) {
            currentPage = page;
            loadData();
        }
    }

    @Override
    public void onNavigate(RouteParams params) {
        loadData();
    }
}

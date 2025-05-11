package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.repositories.CategoryRepository;
import com.nhom4.nhtsstore.repositories.OrderRepository;
import com.nhom4.nhtsstore.repositories.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardStatisticsService implements IDashboardStatisticsService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public DashboardStatisticsService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Map<String, Map<String, Double>> getRevenueComparisonByMonth() {
        Map<String, Map<String, Double>> result = new LinkedHashMap<>();

        // Get current and previous year
        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        // Initialize all months with zero values for both years
        for (int month = 1; month <= 12; month++) {
            Map<String, Double> yearValues = new HashMap<>();
            yearValues.put(String.valueOf(currentYear), 0.0);
            yearValues.put(String.valueOf(previousYear), 0.0);
            result.put(String.format("%02d", month), yearValues);
        }

        LocalDate startDate = LocalDate.of(previousYear, 1, 1);
        Date sqlStartDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate endDate = LocalDate.of(currentYear, 12, 31);
        Date sqlEndDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Object[]> data = orderRepository.getRevenueByMonthAndYear(sqlStartDate, sqlEndDate);

        // Process query results
        for (Object[] row : data) {
            String month = (String) row[0];
            String year = row[1].toString();
            Double revenue = ((Number) row[2]).doubleValue();

            if (result.containsKey(month)) {
                result.get(month).put(year, revenue);
            }
        }

        return result;
    }

    @Override
    public Map<String, Double> getRevenueByTimeFrame() {
        Map<String, Double> revenueByMonth = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

        // Calculate start date (2 years ago)
        LocalDate startDate = LocalDate.now().minusYears(2).withDayOfMonth(1);
        Date sqlStartDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Initialize all months with zero
        LocalDate current = startDate;
        while (!current.isAfter(LocalDate.now())) {
            revenueByMonth.put(current.format(formatter), 0.0);
            current = current.plusMonths(1);
        }

        // Get aggregated data directly from the database
        List<Object[]> data = orderRepository.getRevenueByTimeFrame(
                sqlStartDate, new java.sql.Date(System.currentTimeMillis()));

        // Populate with actual values
        for (Object[] row : data) {
            String month = (String) row[0];
            Double revenue = ((Number) row[1]).doubleValue();
            revenueByMonth.put(month, revenue);
        }

        return revenueByMonth;
    }

    @Override
    public double getTotalRevenue() {
        Double total = orderRepository.getTotalCompletedRevenue();
        return total != null ? total : 0.0;
    }

    @Override
    public double getTotalRevenueForYear(int year) {
        Double total = orderRepository.getTotalCompletedRevenueForYear(year);
        return total != null ? total : 0.0;
    }
    @Override
    public Map<String, Double> getRevenueByCategoryWithLimit(int topCategoriesLimit) {
        // Get total revenue by category
        Map<String, Double> revenueByCategoryMap = new HashMap<>();
        Map<String, Double> result = new LinkedHashMap<>();

        List<Map<String,Object>> categoryRevenue = orderRepository.getCategoryRevenue();

        // Process query results
        for (Map<String, Object> row : categoryRevenue) {
            String categoryName = (String) row.get("Name");
            Double revenue = ((Number) row.get("revenue")).doubleValue();
            revenueByCategoryMap.put(categoryName, revenue);
        }

        if (revenueByCategoryMap.size() > topCategoriesLimit) {
            // Sort categories by revenue
            List<Map.Entry<String, Double>> entries = new ArrayList<>(revenueByCategoryMap.entrySet());
            entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());

            // Add top N categories
            double otherCategoriesRevenue = 0.0;
            for (int i = 0; i < entries.size(); i++) {
                if (i < topCategoriesLimit) {
                    result.put(entries.get(i).getKey(), entries.get(i).getValue());
                } else {
                    // Accumulate the rest into "Other"
                    otherCategoriesRevenue += entries.get(i).getValue();
                }
            }

            // Add the "Other" category if it has value
            if (otherCategoriesRevenue > 0) {
                result.put("Other", otherCategoriesRevenue);
            }

            return result;
        }

        return revenueByCategoryMap;
    }

    @Override
    public List<Map.Entry<String, Integer>> getTopSellingProducts(int limit) {
        // Get products with their sales quantities
        List<Object[]> productSales = orderRepository.getProductSalesCount();

        Map<String, Integer> productSalesMap = new HashMap<>();
        for (Object[] row : productSales) {
            String productName = (String) row[0];
            Integer salesCount = ((Number) row[1]).intValue();
            productSalesMap.put(productName, salesCount);
        }

        // Sort by sales count
        List<Map.Entry<String, Integer>> sortedProducts =
                new ArrayList<>(productSalesMap.entrySet());
        sortedProducts.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


        return sortedProducts.subList(0, Math.min(limit, sortedProducts.size()));
    }

    @Override
    public Map<String, Integer> getOrderStatusCounts() {
        List<Object[]> statusCounts = orderRepository.getOrderStatusCounts();

        Map<String, Integer> result = new HashMap<>();
        for (Object[] row : statusCounts) {
            String status = row[0].toString();
            Integer count = ((Number) row[1]).intValue();
            result.put(status, count);
        }

        return result;
    }

    @Override
    public Map<String, Map<String, Integer>> getInventoryByCategory() {
        List<Category> categories = categoryRepository.findAll();

        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (Category category : categories) {
            Map<String, Integer> statusMap = new HashMap<>();
            statusMap.put("Active", 0);
            statusMap.put("Inactive", 0);
            result.put(category.getName(), statusMap);
        }

        List<Object[]> inventoryCounts = productRepository.getInventoryByCategoryAndStatus();

        for (Object[] row : inventoryCounts) {
            String categoryName = (String) row[0];
            boolean isActive = (boolean) row[1];
            Integer quantity = ((Number) row[2]).intValue();

            String status = isActive ? "Active" : "Inactive";
            result.get(categoryName).put(status, quantity);
        }

        return result;
    }
    @Override
    public Map<String, Double> getAverageOrderValueByMonth() {
        Map<String, Double> result = new LinkedHashMap<>();

        // Calculate start date (2 years ago)
        LocalDate startDate = LocalDate.now().minusYears(2).withDayOfMonth(1);
        Date sqlStartDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        LocalDate current = startDate;
        while (!current.isAfter(LocalDate.now())) {
            result.put(current.format(formatter), 0.0);
            current = current.plusMonths(1);
        }

        List<Object[]> data = orderRepository.getAverageOrderValueByMonth(
                sqlStartDate, new java.sql.Date(System.currentTimeMillis()));

        // Populate data
        for (Object[] row : data) {
            String month = (String) row[0];
            Double avgValue = ((Number) row[1]).doubleValue();
            result.put(month, avgValue);
        }

        return result;
    }

    @Override
    public List<Map.Entry<String, Double>> getProductProfitability(int limit) {
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<Object[]> data = productRepository.getProductProfitability(pageRequest);

        for (Object[] row : data) {
            String productName = (String) row[0];
            Double profit = ((Number) row[3]).doubleValue();
            result.add(new AbstractMap.SimpleEntry<>(productName, profit));
        }

        return result;
    }

    @Override
    public Map<String, Double> getSalesBySupplier() {
        Map<String, Double> result = new LinkedHashMap<>();

        List<Object[]> data = orderRepository.getSalesBySupplier();

        for (Object[] row : data) {
            String supplierName = (String) row[0];
            Double sales = ((Number) row[1]).doubleValue();
            result.put(supplierName, sales);
        }

        return result;
    }
}
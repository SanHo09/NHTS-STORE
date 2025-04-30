package com.nhom4.nhtsstore.services;

import java.util.List;
import java.util.Map;

public interface IDashboardStatisticsService {
    Map<String, Double> getRevenueByTimeFrame(int numberOfMonths);
    Map<String, Double> getRevenueByCategoryWithLimit(int topCategoriesLimit);
    List<Map.Entry<String, Integer>> getTopSellingProducts(int limit);
    Map<String, Integer> getOrderStatusCounts();
    Map<String, Map<String, Integer>> getInventoryByCategory();
    Map<String, Double> getAverageOrderValueByMonth(int numberOfMonths);
    List<Map.Entry<String, Double>> getProductProfitability(int limit);
    Map<String, Double> getSalesBySupplier();
}

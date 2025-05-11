package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Factory for creating order-related charts used in the dashboard.
 */
public class OrderChartFactory {

    private final IDashboardStatisticsService dashboardStatistics;
    private final LanguageManager languageManager;
    private final ChartUtility chartUtility;

    public OrderChartFactory(IDashboardStatisticsService dashboardStatistics, LanguageManager languageManager) {
        this.dashboardStatistics = dashboardStatistics;
        this.languageManager = languageManager;
        this.chartUtility = new ChartUtility(languageManager);
    }

    /**
     * Creates a pie chart showing order status distribution
     */
    public PieChart createOrderStatusChart() {
        try {
            return JavaFxSwing.runAndReturn(() -> {
                try {
                    // Create the pie chart with empty data
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                    final PieChart chart = new PieChart(pieChartData);
                    chart.setTitle(languageManager.getText("dashboard.chart.order_status"));
                    chart.setPrefHeight(300);
                    
                    // Get order status data
                    Map<String, Integer> orderStatusCount = dashboardStatistics.getOrderStatusCounts();
                    // Calculate total for percentage calculations
                    double total = orderStatusCount.values().stream().mapToDouble(Integer::doubleValue).sum();
                    
                    // Add data to chart
                    for (Map.Entry<String, Integer> entry : orderStatusCount.entrySet()) {
                        PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                        pieChartData.add(slice);
                    }
                    
                    // Add percentage labels
                    chartUtility.addPieChartPercentages(chart, total);
                    
                    return chart;
                } catch (Exception e) {
                    System.err.println("Error creating order status chart: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error executing chart creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 
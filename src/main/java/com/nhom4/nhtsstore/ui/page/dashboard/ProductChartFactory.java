package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Factory for creating product-related charts used in the dashboard.
 */
public class ProductChartFactory {

    private final IDashboardStatisticsService dashboardStatistics;
    private final LanguageManager languageManager;
    private final ChartUtility chartUtility;

    public ProductChartFactory(IDashboardStatisticsService dashboardStatistics, LanguageManager languageManager) {
        this.dashboardStatistics = dashboardStatistics;
        this.languageManager = languageManager;
        this.chartUtility = new ChartUtility(languageManager);
    }

    /**
     * Creates a bar chart showing top selling products
     */
    public BarChart<String, Number> createTopProductsChart() {
        try {
            return JavaFxSwing.runAndReturn(() -> {
                try {
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel(languageManager.getText("dashboard.chart.product"));

                    final NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel(languageManager.getText("dashboard.chart.sales_quantity"));

                    final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                    barChart.setTitle(languageManager.getText("dashboard.chart.top_products"));
                    barChart.setPrefHeight(300);

                    // Create series
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(languageManager.getText("dashboard.chart.sales_quantity"));
                    
                    // Get data and populate chart
                    List<Map.Entry<String, Integer>> topProducts = dashboardStatistics.getTopSellingProducts(10);
                    
                    for (Map.Entry<String, Integer> entry : topProducts) {
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    }
                    
                    // Add series to chart
                    barChart.getData().add(series);
                    
                    // Add tooltips
                    chartUtility.addBarChartTooltips(barChart);
                    
                    return barChart;
                } catch (Exception e) {
                    System.err.println("Error creating top products chart: " + e.getMessage());
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

    /**
     * Creates a stacked bar chart showing inventory by category
     */
    @SneakyThrows
    public StackedBarChart<String, Number> createInventoryByCategoryChart() {
        return JavaFxSwing.runAndReturn(() -> {
            try {
                final CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel(languageManager.getText("dashboard.chart.category"));

                final NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel(languageManager.getText("dashboard.chart.inventory"));

                final StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
                stackedBarChart.setTitle(languageManager.getText("dashboard.chart.inventory_by_category"));
                stackedBarChart.setPrefHeight(300);

                // Create empty series first
                XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
                activeSeries.setName(languageManager.getText("dashboard.chart.active_products"));

                XYChart.Series<String, Number> inactiveSeries = new XYChart.Series<>();
                inactiveSeries.setName(languageManager.getText("dashboard.chart.inactive_products"));

                // Add empty series to chart
                stackedBarChart.getData().addAll(activeSeries, inactiveSeries);
                Map<String, Map<String, Integer>> inventoryData = dashboardStatistics.getInventoryByCategory();

                // Clear existing data first
                activeSeries.getData().clear();
                inactiveSeries.getData().clear();

                for (Map.Entry<String, Map<String, Integer>> entry : inventoryData.entrySet()) {
                    String categoryName = entry.getKey();
                    Map<String, Integer> statusMap = entry.getValue();

                    activeSeries.getData().add(new XYChart.Data<>(categoryName, statusMap.get("Active")));
                    inactiveSeries.getData().add(new XYChart.Data<>(categoryName, statusMap.get("Inactive")));
                }

                // Add tooltips after data is loaded
                chartUtility.addStackedBarChartTooltips(stackedBarChart);
                return stackedBarChart;
            } catch (Exception e) {
                System.err.println("Error loading inventory chart data: " + e.getMessage());
                return null; // Return null in case of error
            }
        });
    }

    /**
     * Creates a bar chart showing product profitability
     */
    public BarChart<String, Number> createProductProfitabilityChart() {
        try {
            return JavaFxSwing.runAndReturn(() -> {
                try {
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel(languageManager.getText("dashboard.chart.product"));

                    final NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel(languageManager.getText("dashboard.chart.profit"));

                    final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                    barChart.setTitle(languageManager.getText("dashboard.chart.product_profit"));
                    
                    // Create series
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(languageManager.getText("dashboard.chart.profit"));
                    
                    // Get data and populate chart
                    List<Map.Entry<String, Double>> data = dashboardStatistics.getProductProfitability(10);
                    
                    for (Map.Entry<String, Double> entry : data) {
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    }
                    
                    // Add series to chart
                    barChart.getData().add(series);
                    
                    // Add tooltips
                    chartUtility.addBarChartTooltips(barChart);
                    
                    return barChart;
                } catch (Exception e) {
                    System.err.println("Error creating product profitability chart: " + e.getMessage());
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
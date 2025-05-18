package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Factory for creating revenue-related charts used in the dashboard.
 */
public class RevenueProfitChartFactory {

    private final IDashboardStatisticsService dashboardStatistics;
    private final LanguageManager languageManager;
    private final ChartUtility chartUtility;

    public RevenueProfitChartFactory(IDashboardStatisticsService dashboardStatistics, LanguageManager languageManager) {
        this.dashboardStatistics = dashboardStatistics;
        this.languageManager = languageManager;
        this.chartUtility = new ChartUtility(languageManager);
    }

    @SneakyThrows
    public Region createRevenueComparisonChart() {
        return JavaFxSwing.runAndReturn(() -> {
            try {
                VBox container = new VBox(10);
                HBox header = new HBox();
                header.setAlignment(Pos.CENTER_LEFT);

                // Create chart
                final CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel(languageManager.getText("dashboard.chart.month"));

                final NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel(languageManager.getText("dashboard.chart.revenue"));

                final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle(languageManager.getText("dashboard.chart.revenue_comparison"));
                barChart.setAnimated(false);
                barChart.setCategoryGap(8);
                barChart.setBarGap(4);
                
                // Create text components for header
                Text titleText = new Text(languageManager.getText("dashboard.current_month_revenue"));
                titleText.getStyleClass().add("card-title");
                
                // Get data for calculations
                Map<String, Map<String, Double>> revenueData = dashboardStatistics.getRevenueComparisonByMonth();
                int currentYear = LocalDate.now().getYear();
                int previousYear = currentYear - 1;
                int currentMonth = LocalDate.now().getMonthValue();
                String currentMonthKey = String.format("%02d", currentMonth);

                // Calculate current and previous month revenue
                double currentMonthRevenue = 0.0;
                double previousMonthRevenue = 0.0;

                if (revenueData.containsKey(currentMonthKey)) {
                    currentMonthRevenue = revenueData.get(currentMonthKey).getOrDefault(String.valueOf(currentYear), 0.0);

                    // Get previous month revenue
                    int prevMonth = currentMonth - 1;
                    if (prevMonth < 1) {
                        prevMonth = 12;
                        String prevMonthKey = String.format("%02d", prevMonth);
                        previousMonthRevenue = revenueData.get(prevMonthKey).getOrDefault(String.valueOf(previousYear), 0.0);
                    } else {
                        String prevMonthKey = String.format("%02d", prevMonth);
                        previousMonthRevenue = revenueData.get(prevMonthKey).getOrDefault(String.valueOf(currentYear), 0.0);
                    }
                }

                // Format revenue value
                String formattedRevenue = UIUtils.formatCurrency(currentMonthRevenue);
                Text valueText = new Text(formattedRevenue);
                valueText.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
                
                // Calculate percentage change
                String compareText;
                String colorStyle;

                if (previousMonthRevenue > 0) {
                    double percentChange = ((currentMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100;
                    String arrow = percentChange >= 0 ? "↑" : "↓";
                    colorStyle = percentChange >= 0 ? "-fx-fill: #2E7D32;" : "-fx-fill: #C62828;";
                    compareText = String.format("%s %.1f%% than last month", arrow, Math.abs(percentChange));
                } else {
                    compareText = languageManager.getText("dashboard.no_comparison_data");
                    colorStyle = "-fx-fill: #757575;";
                }

                Text comparisonText = new Text(compareText);
                comparisonText.setStyle(colorStyle + " -fx-font-size: 12px;");
                
                // Create series
                XYChart.Series<String, Number> currentYearSeries = new XYChart.Series<>();
                currentYearSeries.setName(languageManager.getText("dashboard.revenue_year") + " " + currentYear);

                XYChart.Series<String, Number> previousYearSeries = new XYChart.Series<>();
                previousYearSeries.setName(languageManager.getText("dashboard.revenue_prev_year") + " " + previousYear);

                // Add data to series
                String[] monthNames = chartUtility.getLocalizedMonthNames();
                for (int month = 1; month <= 12; month++) {
                    String monthKey = String.format("%02d", month);
                    String monthName = monthNames[month-1];

                    if (revenueData.containsKey(monthKey)) {
                        Map<String, Double> yearValues = revenueData.get(monthKey);
                        double prevYearValue = yearValues.getOrDefault(String.valueOf(previousYear), 0.0);
                        double currYearValue = yearValues.getOrDefault(String.valueOf(currentYear), 0.0);
                        previousYearSeries.getData().add(new XYChart.Data<>(monthName, prevYearValue));
                        currentYearSeries.getData().add(new XYChart.Data<>(monthName, currYearValue));
                    }
                }

                // Add series to chart
                barChart.getData().add(previousYearSeries);
                barChart.getData().add(currentYearSeries);
                
                // Add tooltips
                chartUtility.addBarChartTooltips(barChart);
                
                // Assemble UI components
                VBox headerTextBox = new VBox(2);
                headerTextBox.setAlignment(Pos.CENTER_LEFT);
                headerTextBox.getChildren().addAll(titleText, valueText, comparisonText);
                header.getChildren().add(headerTextBox);
                
                container.getChildren().addAll(header, barChart);
                
                return container;
            } catch (Exception e) {
                System.err.println("Error creating revenue comparison chart: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }

    @SneakyThrows
    public AreaChart<String, Number> createProfitMonthly() {
        return JavaFxSwing.runAndReturn(() -> {
            try {
                final CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel(languageManager.getText("dashboard.chart.month"));

                final NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel(languageManager.getText("dashboard.chart.profit"));

                final AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
                areaChart.setTitle(languageManager.getText("dashboard.chart.monthly_profit"));
                areaChart.setCreateSymbols(true);

                // Add CSS styling directly to the chart
                areaChart.setStyle("-fx-stroke-width: 2;");

                // Get profit data
                Map<String, Double> data = dashboardStatistics.getMonthlyProfitData();
                Map<Integer, Map<String, Double>> profitsByYear = new HashMap<>();

                // Process data by year and month
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    String[] parts = entry.getKey().split("/");
                    if (parts.length == 2) {
                        String month = parts[0];
                        int year = Integer.parseInt(parts[1]);
                        profitsByYear.putIfAbsent(year, new HashMap<>());
                        profitsByYear.get(year).put(month, entry.getValue());
                    }
                }

                // Create series for each year with complete month sets
                String[] monthNames = chartUtility.getLocalizedMonthNames();

                // Sort years in reverse order (newer years first)
                Integer[] years = profitsByYear.keySet().toArray(new Integer[0]);
                java.util.Arrays.sort(years, java.util.Collections.reverseOrder());

                for (Integer year : years) {
                    Map<String, Double> yearData = profitsByYear.get(year);
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(languageManager.getText("dashboard.chart.profit") + " " + year);

                    // Ensure all months have entries
                    for (int i = 1; i <= 12; i++) {
                        String monthKey = String.format("%02d", i);
                        String monthName = monthNames[i-1];

                        // Get actual value or use 0 if month has no data
                        Double value = yearData.getOrDefault(monthKey, 0.0);
                        series.getData().add(new XYChart.Data<>(monthName, value));
                    }

                    areaChart.getData().add(series);
                }

                // Add tooltips using ChartUtility
                chartUtility.addLineChartTooltips(areaChart);

                // Apply post-processing to make data points visible on top of areas
                javafx.application.Platform.runLater(() -> {
                    for (XYChart.Series<String, Number> series : areaChart.getData()) {
                        for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                            Node node = dataPoint.getNode();
                            if (node != null) {
                                // Make symbols always on top
                                node.setViewOrder(-1);
                                // Increase size and visibility
                                node.setStyle("-fx-background-radius: 5px; -fx-padding: 5px;");
                            }
                        }
                    }
                });

                return areaChart;
            } catch (Exception e) {
                System.err.println("Error creating profit chart: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }

    public PieChart createRevenueByCategoryChart() {
        try {
            return JavaFxSwing.runAndReturn(() -> {
                try {
                    // Create the pie chart
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                    final PieChart chart = new PieChart(pieChartData);
                    chart.setTitle(languageManager.getText("dashboard.chart.revenue_by_category"));
                    chart.setPrefHeight(300);
                    
                    // Get data
                    Map<String, Double> revenueByCategoryMap = dashboardStatistics.getRevenueByCategoryWithLimit(10);
                    
                    // Calculate total for percentage calculations
                    double total = revenueByCategoryMap.values().stream().mapToDouble(Double::doubleValue).sum();
                    
                    // Add data to chart
                    for (Map.Entry<String, Double> entry : revenueByCategoryMap.entrySet()) {
                        PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                        pieChartData.add(slice);
                    }
                    
                    // Add percentage labels
                    chartUtility.addPieChartPercentages(chart, total);
                    
                    return chart;
                } catch (Exception e) {
                    System.err.println("Error creating revenue by category chart: " + e.getMessage());
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

    public PieChart createSalesBySupplierChart() {
        try {
            return JavaFxSwing.runAndReturn(() -> {
                try {
                    // Create the pie chart
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                    final PieChart chart = new PieChart(pieChartData);
                    chart.setTitle(languageManager.getText("dashboard.chart.revenue_by_supplier"));
                    
                    // Get data
                    Map<String, Double> data = dashboardStatistics.getSalesBySupplier();
                    
                    // Calculate total for percentage calculations
                    double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
                    
                    // Add data to chart
                    for (Map.Entry<String, Double> entry : data.entrySet()) {
                        PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                        pieChartData.add(slice);
                    }
                    
                    // Add percentage labels
                    chartUtility.addPieChartPercentages(chart, total);
                    
                    return chart;
                } catch (Exception e) {
                    System.err.println("Error creating sales by supplier chart: " + e.getMessage());
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
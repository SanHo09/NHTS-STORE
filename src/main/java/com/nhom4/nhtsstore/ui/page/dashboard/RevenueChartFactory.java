package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.utils.JavaFxSwing;
import com.nhom4.nhtsstore.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Factory for creating revenue-related charts used in the dashboard.
 */
public class RevenueChartFactory {

    private final IDashboardStatisticsService dashboardStatistics;
    private final LanguageManager languageManager;
    private final ChartUtility chartUtility;

    public RevenueChartFactory(IDashboardStatisticsService dashboardStatistics, LanguageManager languageManager) {
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
    public AreaChart<String, Number> createAverageOrderValueChart() {
        return JavaFxSwing.runAndReturn(() -> {
            try {
                final CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel(languageManager.getText("dashboard.chart.month"));

                final NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel(languageManager.getText("dashboard.chart.order_value"));

                final AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
                areaChart.setTitle(languageManager.getText("dashboard.chart.average_order"));
                areaChart.setCreateSymbols(true); // Show data points on the line

                // Get data
                Map<String, Double> data = dashboardStatistics.getAverageOrderValueByMonth();
                Map<Integer, Map<String, Double>> valuesByYear = new HashMap<>();

                // Process the data to separate by year
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    String key = entry.getKey();

                    // Parse the key to extract year and month (format "MM/YYYY")
                    String[] parts = key.split("/");
                    if (parts.length == 2) {
                        String month = parts[0];
                        int year = Integer.parseInt(parts[1]);

                        // Create year entry if it doesn't exist
                        valuesByYear.putIfAbsent(year, new HashMap<>());

                        // Add data to appropriate year
                        valuesByYear.get(year).put(month, entry.getValue());
                    }
                }

                // Create a series for each year
                for (Map.Entry<Integer, Map<String, Double>> yearEntry : valuesByYear.entrySet()) {
                    int year = yearEntry.getKey();
                    Map<String, Double> monthData = yearEntry.getValue();

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(languageManager.getText("dashboard.chart.avg_value") + " " + year);

                    // Sort months and add data points
                    List<Map.Entry<String, Double>> sortedMonths = new ArrayList<>(monthData.entrySet());
                    sortedMonths.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())));

                    for (Map.Entry<String, Double> monthEntry : sortedMonths) {
                        series.getData().add(new XYChart.Data<>(monthEntry.getKey(), monthEntry.getValue()));
                    }

                    areaChart.getData().add(series);
                }

                // Add tooltips
                chartUtility.addLineChartTooltips(areaChart);

                return areaChart;
            } catch (Exception e) {
                System.err.println("Error creating average order value chart: " + e.getMessage());
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
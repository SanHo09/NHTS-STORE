package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.util.function.Supplier;

/**
 * JavaFX view component for the dashboard that will be embedded in the Swing panel.
 */
public class DashBoardFxView extends JFXPanel {
    private final IDashboardStatisticsService dashboardStatistics;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    
    // UI components
    private GridPane grid;
    private ScrollPane scrollPane;
    private StackPane rootPane;
    private MFXProgressSpinner loadingSpinner;
    
    // Chart factories
    private final StatisticsCardFactory statisticsCardFactory;
    private final RevenueChartFactory revenueChartFactory;
    private final ProductChartFactory productChartFactory;
    private final OrderChartFactory orderChartFactory;

    public DashBoardFxView(
            IDashboardStatisticsService dashboardStatistics,
            ThemeManager themeManager,
            LanguageManager languageManager) {
        this.dashboardStatistics = dashboardStatistics;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
        
        // Initialize chart factories
        this.statisticsCardFactory = new StatisticsCardFactory(dashboardStatistics, languageManager);
        this.revenueChartFactory = new RevenueChartFactory(dashboardStatistics, languageManager);
        this.productChartFactory = new ProductChartFactory(dashboardStatistics, languageManager);
        this.orderChartFactory = new OrderChartFactory(dashboardStatistics, languageManager);
        
        SwingUtilities.invokeLater(() -> {
            Platform.runLater(this::initComponents);
        });
    }

    public void refreshData() {
        showLoadingSpinner();

        // Schedule the rebuild on a virtual thread to avoid blocking the UI
        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(100); // Small delay to ensure loading spinner is visible

                // Update UI on JavaFX thread when ready
                Platform.runLater(() -> {
                    try {
                        rebuildCharts();
                        hideLoadingSpinner();
                        scrollToTop();
                    } catch (Exception e) {
                        System.err.println("Error rebuilding charts: " + e.getMessage());
                        e.printStackTrace();
                        hideLoadingSpinner();
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    hideLoadingSpinner();
                    scrollToTop();
                });
            }
        });
    }
    
    private void scrollToTop() {
        if (scrollPane != null) {
            scrollPane.setVvalue(0);
            scrollPane.setHvalue(0);
        }
    }
    
    private void initComponents() {
        rootPane = new StackPane();
        rootPane.setAlignment(Pos.CENTER);
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setMaxWidth(1960);

        // Set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(column1, column2);

        // Add to scroll pane
        scrollPane = new ScrollPane();
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(false);
        
        // Create loading spinner
        loadingSpinner = new MFXProgressSpinner();
        loadingSpinner.setRadius(30);
        loadingSpinner.setVisible(false);
        StackPane.setAlignment(loadingSpinner, Pos.CENTER);

        // Add components to root pane
        rootPane.getChildren().addAll(scrollPane, loadingSpinner);

        Scene scene = new Scene(rootPane);
        this.setScene(scene);
        JavaFxThemeUtil.setupThemeListener(rootPane, themeManager);
        
        // Listen for language changes
        languageManager.addLanguageChangeListener(this::updateTexts);
        
        // Build charts
        rebuildCharts();
    }
    
    /**
     * Update all text elements when language changes
     */
    private void updateTexts() {
        Platform.runLater(() -> {
            try {
                // Rebuild charts to update all text
                rebuildCharts();
            } catch (Exception e) {
                System.err.println("Error updating dashboard texts: " + e.getMessage());
            }
        });
    }

    private void showLoadingSpinner() {
        loadingSpinner.setVisible(true);
    }

    private void hideLoadingSpinner() {
        loadingSpinner.setVisible(false);
    }
    
    private void rebuildCharts() {
        // Clear existing content first
        grid.getChildren().clear();

        VBox chartsContainer = new VBox(20);
        chartsContainer.setPadding(new Insets(0));

        // Add statistics cards at the top
        Region revenueCards = createRevenueStatisticsCards();
        chartsContainer.getChildren().add(revenueCards);

        // Revenue charts row
        createAndAddChartRow(chartsContainer, 1,
                "dashboard.chart.revenue_comparison", revenueChartFactory::createRevenueComparisonChart,
                "dashboard.chart.average_order", revenueChartFactory::createAverageOrderValueChart);

        // Product and order status charts row
        createAndAddChartRow(chartsContainer, 2,
                "dashboard.chart.top_products", productChartFactory::createTopProductsChart,
                "dashboard.chart.order_status", orderChartFactory::createOrderStatusChart);

        // Revenue categories and inventory charts row
        createAndAddChartRow(chartsContainer, 3,
                "dashboard.chart.revenue_by_category", revenueChartFactory::createRevenueByCategoryChart,
                "dashboard.chart.inventory_by_category", productChartFactory::createInventoryByCategoryChart);

        // Profitability and supplier charts row
        createAndAddChartRow(chartsContainer, 4,
                "dashboard.chart.product_profit", productChartFactory::createProductProfitabilityChart,
                "dashboard.chart.revenue_by_supplier", revenueChartFactory::createSalesBySupplierChart);

        grid.add(chartsContainer, 0, 0, 2, 1);
        
        // Request garbage collection after rebuilding all charts to free memory
        System.gc();
    }
    
    private void createAndAddChartRow(VBox container, int rowIndex,
                                      String titleKey1, Supplier<Region> chartFactory1,
                                      String titleKey2, Supplier<Region> chartFactory2) {
        HBox row = new HBox(20);
        row.setPrefHeight(500);

        String title1 = languageManager.getText(titleKey1);
        String title2 = languageManager.getText(titleKey2);
        
        StackPane chart1Container = ChartContainerFactory.createLazyLoadingChart(title1, chartFactory1);
        StackPane chart2Container = ChartContainerFactory.createLazyLoadingChart(title2, chartFactory2);

        HBox.setHgrow(chart1Container, Priority.ALWAYS);
        HBox.setHgrow(chart2Container, Priority.ALWAYS);

        row.getChildren().addAll(chart1Container, chart2Container);

        if (rowIndex > 0) {
            Separator separator = new Separator();
            separator.setPrefHeight(10);
            separator.setPadding(new Insets(10, 0, 10, 0));
            container.getChildren().add(separator);
        }

        container.getChildren().add(row);
    }
    
    private Region createRevenueStatisticsCards() {
        HBox cardsRow = new HBox(20);
        cardsRow.setPrefHeight(220);

        StackPane revenueCardContainer = ChartContainerFactory.createLazyLoadingCardContainer(
                languageManager.getText("dashboard.revenue_stats"), 
                statisticsCardFactory::createTotalRevenueCard);
                
        StackPane orderStatsContainer = ChartContainerFactory.createLazyLoadingCardContainer(
                languageManager.getText("dashboard.order_stats"), 
                statisticsCardFactory::createOrderStatisticsCard);
                
        StackPane productStatsContainer = ChartContainerFactory.createLazyLoadingCardContainer(
                languageManager.getText("dashboard.product_stats"), 
                statisticsCardFactory::createProductStatisticsCard);

        HBox.setHgrow(revenueCardContainer, Priority.ALWAYS);
        HBox.setHgrow(orderStatsContainer, Priority.ALWAYS);
        HBox.setHgrow(productStatsContainer, Priority.ALWAYS);

        cardsRow.getChildren().addAll(revenueCardContainer, orderStatsContainer, productStatsContainer);
        return cardsRow;
    }
} 
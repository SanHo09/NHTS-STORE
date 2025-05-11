package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.utils.NumberAnimationUtils;
import com.nhom4.nhtsstore.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.util.Map;

/**
 * Factory for creating statistic cards used in the dashboard.
 */
public class StatisticsCardFactory {

    private final IDashboardStatisticsService dashboardStatistics;
    private final LanguageManager languageManager;

    public StatisticsCardFactory(IDashboardStatisticsService dashboardStatistics, LanguageManager languageManager) {
        this.dashboardStatistics = dashboardStatistics;
        this.languageManager = languageManager;
    }

    @SneakyThrows
    public Region createTotalRevenueCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("dashboard-card");
        Text titleText = new Text(languageManager.getText("dashboard.revenue_title"));
        titleText.getStyleClass().add("card-title");
        double totalRevenue = dashboardStatistics.getTotalRevenue();
        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;
        double currentYearRevenue = dashboardStatistics.getTotalRevenueForYear(currentYear);
        double previousYearRevenue = dashboardStatistics.getTotalRevenueForYear(previousYear);
        HBox totalRevenueRow = createMetricRow(languageManager.getText("dashboard.total_revenue"),
                UIUtils.formatCurrency(totalRevenue), "#1565C0");

        HBox currentYearRow = createMetricRow(languageManager.getText("dashboard.revenue_year") + " " + currentYear,
                UIUtils.formatCurrency(currentYearRevenue), "#2E7D32");

        HBox previousYearRow = createMetricRow(languageManager.getText("dashboard.revenue_prev_year") + " " + previousYear,
                UIUtils.formatCurrency(previousYearRevenue), "#7B1FA2");
        Text totalRevenueText = (Text) totalRevenueRow.getChildren().get(2);
        Text currentYearText = (Text) currentYearRow.getChildren().get(2);
        Text previousYearText = (Text) previousYearRow.getChildren().get(2);

        // Create and start animations
        NumberAnimationUtils.animateCurrency(totalRevenueText, totalRevenue);
        NumberAnimationUtils.animateCurrency(currentYearText, currentYearRevenue);
        NumberAnimationUtils.animateCurrency(previousYearText, previousYearRevenue);
        HBox comparisonRow = new HBox(10);
        comparisonRow.setAlignment(Pos.CENTER_LEFT);

        String comparisonText = "";
        String comparisonColor = "#757575";

        if (previousYearRevenue > 0) {
            double percentChange = ((currentYearRevenue - previousYearRevenue) / previousYearRevenue) * 100;
            String direction = percentChange >= 0 ? "↑" : "↓";
            comparisonColor = percentChange >= 0 ? "#2E7D32" : "#C62828";
            comparisonText = languageManager.getText("dashboard.compared_to_prev") + ": " + direction + " " + String.format("%.1f%%", Math.abs(percentChange));
        }

        Text comparisonLabel = new Text(comparisonText);
        comparisonLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: " + comparisonColor);
        comparisonRow.getChildren().add(comparisonLabel);

        card.getChildren().addAll(
                titleText,
                new Separator(),
                totalRevenueRow,
                currentYearRow,
                previousYearRow,
                comparisonRow
        );

        return card;
    }

    @SneakyThrows
    public Region createOrderStatisticsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("dashboard-card");
        // Title
        Text titleText = new Text(languageManager.getText("dashboard.order_stats"));
        titleText.getStyleClass().add("card-title");
        // Get order status data
        Map<String, Integer> orderStatusCounts = dashboardStatistics.getOrderStatusCounts();
        int totalOrders = orderStatusCounts.values().stream().mapToInt(Integer::intValue).sum();
        int completedOrders = orderStatusCounts.getOrDefault("COMPLETED", 0);
        int inProgressOrders = orderStatusCounts.getOrDefault("IN_PROGRESS", 0);

        // Calculate completion rate
        double completionRate = totalOrders > 0 ? (completedOrders * 100.0 / totalOrders) : 0;

        // Create metrics
        HBox totalOrdersRow = createMetricRow(languageManager.getText("dashboard.total_orders"),
                String.valueOf(totalOrders), "#1565C0");

        HBox completedOrdersRow = createMetricRow(languageManager.getText("dashboard.completed_orders"),
                String.valueOf(completedOrders), "#2E7D32");

        HBox inProgressOrdersRow = createMetricRow(languageManager.getText("dashboard.in_progress_orders"),
                String.valueOf(inProgressOrders), "#FFA000");

        HBox completionRateRow = createMetricRow(languageManager.getText("dashboard.completion_rate"),
                String.format("%.1f%%", completionRate), "#7B1FA2");

        Text completionRateText = (Text) completionRateRow.getChildren().get(2);
        Text totalOrdersText = (Text) totalOrdersRow.getChildren().get(2);
        Text completedOrdersText = (Text) completedOrdersRow.getChildren().get(2);
        Text inProgressOrdersText = (Text) inProgressOrdersRow.getChildren().get(2);
        // Create and start animations
        NumberAnimationUtils.animateInteger(totalOrdersText, totalOrders);
        NumberAnimationUtils.animateInteger(completedOrdersText, completedOrders);
        NumberAnimationUtils.animateInteger(inProgressOrdersText, inProgressOrders);
        NumberAnimationUtils.animatePercentage(completionRateText, completionRate);

        card.getChildren().addAll(
                titleText,
                new Separator(),
                totalOrdersRow,
                completedOrdersRow,
                inProgressOrdersRow,
                completionRateRow
        );

        return card;
    }

    @SneakyThrows
    public Region createProductStatisticsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("dashboard-card");

        // Title
        Text titleText = new Text(languageManager.getText("dashboard.product_stats"));
        titleText.getStyleClass().add("card-title");

        // Get product data
        Map<String, Map<String, Integer>> inventoryData = dashboardStatistics.getInventoryByCategory();
        // Calculate totals
        int totalActiveProducts = 0;
        int totalInactiveProducts = 0;
        int totalInventory = 0;

        for (Map<String, Integer> categoryData : inventoryData.values()) {
            int active = categoryData.getOrDefault("Active", 0);
            int inactive = categoryData.getOrDefault("Inactive", 0);

            totalActiveProducts += active;
            totalInactiveProducts += inactive;
            totalInventory += (active + inactive);
        }

        // Get top selling product if available
        String topProductName = languageManager.getText("dashboard.no_comparison_data");
        int topProductSales = 0;

        var topProducts = dashboardStatistics.getTopSellingProducts(1);
        if (!topProducts.isEmpty()) {
            var topProduct = topProducts.get(0);
            topProductName = topProduct.getKey();
            topProductSales = topProduct.getValue();
        }

        // Create metrics
        HBox totalProductsRow = createMetricRow(languageManager.getText("dashboard.total_products"),
                String.valueOf(totalInventory), "#1565C0");

        HBox activeProductsRow = createMetricRow(languageManager.getText("dashboard.active_products"),
                String.valueOf(totalActiveProducts), "#2E7D32");

        HBox inactiveProductsRow = createMetricRow(languageManager.getText("dashboard.inactive_products"),
                String.valueOf(totalInactiveProducts), "#C62828");

        HBox topSellingRow = createMetricRow(languageManager.getText("dashboard.top_selling"),
                topProductName + " (" + topProductSales + ")", "#FF6F00");

        Text totalProductsText = (Text) totalProductsRow.getChildren().get(2);
        Text activeProductsText = (Text) activeProductsRow.getChildren().get(2);
        Text inactiveProductsText = (Text) inactiveProductsRow.getChildren().get(2);
        // Create and start animations
        NumberAnimationUtils.animateInteger(totalProductsText, totalInventory);
        NumberAnimationUtils.animateInteger(activeProductsText, totalActiveProducts);
        NumberAnimationUtils.animateInteger(inactiveProductsText, totalInactiveProducts);

        card.getChildren().addAll(
                titleText,
                new Separator(),
                totalProductsRow,
                activeProductsRow,
                inactiveProductsRow,
                topSellingRow
        );

        return card;
    }

    private HBox createMetricRow(String label, String value, String color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(10);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("metric-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text valueText = new Text(value);
        valueText.getStyleClass().add("card-value");
        valueText.setStyle("-fx-fill: " + color + ";"); // Keep color as is for metrics

        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
} 
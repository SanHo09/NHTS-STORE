package com.nhom4.nhtsstore.ui.page.dashboard;
import com.nhom4.nhtsstore.services.IDashboardStatisticsService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.geometry.Insets;
import com.nhom4.nhtsstore.entities.*;
import com.nhom4.nhtsstore.enums.OrderStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
@Scope("prototype")
@Controller
public class DashBoardFxController extends JFXPanel {
    private final IDashboardStatisticsService dashboardStatistics;

    public DashBoardFxController(IDashboardStatisticsService dashboardStatistics) {
        this.dashboardStatistics = dashboardStatistics;
        Platform.runLater(this::initComponents);
    }

    private void initComponents() {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setMaxWidth(1960);

        // Create and set up charts
        LineChart<String, Number> revenueChart = createRevenueOverTimeChart();
        PieChart categoryChart = createRevenueByCategoryChart();
        BarChart<String, Number> topProductsChart = createTopProductsChart();
        PieChart orderStatusChart = createOrderStatusChart();
        LineChart<String, Number> avgOrderValueChart = createAverageOrderValueChart();
        StackedBarChart<String, Number> inventoryChart = createInventoryByCategoryChart();
        PieChart supplierSalesChart = createSalesBySupplierChart();
        BarChart<String, Number> profitabilityChart = createProductProfitabilityChart();

        // Apply standard size to all charts
        List<Chart> charts = Arrays.asList(
                revenueChart, categoryChart, topProductsChart, orderStatusChart,
                avgOrderValueChart, inventoryChart, supplierSalesChart, profitabilityChart
        );
        charts.forEach(chart -> setChartSize(chart, 380));


        int row = 0;
        addChartRow(grid, row++,
                "1. Biểu đồ doanh thu theo thời gian", revenueChart,
                "2. Biểu đồ phân bố doanh thu theo danh mục", categoryChart);
        grid.add(createSeparator(), 0, row++, 2, 1);

        addChartRow(grid, row++,
                "3. Top 10 sản phẩm bán chạy", topProductsChart,
                "4. Biểu đồ trạng thái đơn hàng", orderStatusChart);
        grid.add(createSeparator(), 0, row++, 2, 1);

        addChartRow(grid, row++,
                "5. Giá trị trung bình đơn hàng theo thời gian", avgOrderValueChart,
                "6. Số lượng tồn kho theo danh mục", inventoryChart);
        grid.add(createSeparator(), 0, row++, 2, 1);

        addChartRow(grid, row++,
                "7. Doanh thu theo nhà cung cấp", supplierSalesChart,
                "8. Top 10 sản phẩm có lợi nhuận cao nhất", profitabilityChart);

        // Set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(column1, column2);

        // Add to scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane);
        setScene(scene);
    }


    private void addChartRow(GridPane grid, int rowIndex,
                             String title1, Chart chart1,
                             String title2, Chart chart2) {
        // Left column
        VBox leftBox = new VBox(5);
        Text leftTitle = new Text(title1);
        leftBox.getChildren().addAll(leftTitle, chart1);
        grid.add(leftBox, 0, rowIndex);

        // Right column
        VBox rightBox = new VBox(5);
        Text rightTitle = new Text(title2);
        rightBox.getChildren().addAll(rightTitle, chart2);
        grid.add(rightBox, 1, rowIndex);
    }


    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setPrefHeight(10);
        separator.setPadding(new Insets(10, 0, 10, 0));
        return separator;
    }


    private void setChartSize(Chart chart, double height) {
        chart.setPrefHeight(height);
        chart.setMaxHeight(height);
        chart.setMinHeight(height);
        chart.setPrefWidth(USE_COMPUTED_SIZE);
        chart.setMaxWidth(900);
    }

    private LineChart<String, Number> createRevenueOverTimeChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh thu (USD)");

        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Doanh thu theo tháng");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu 2024");

        Map<String, Double> revenueByMonth = dashboardStatistics.getRevenueByTimeFrame(12);

        for (Map.Entry<String, Double> entry : revenueByMonth.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);
        lineChart.setPrefHeight(300);

        return lineChart;
    }

    private PieChart createRevenueByCategoryChart() {

        Map<String, Double> revenueByCategoryMap = dashboardStatistics.getRevenueByCategoryWithLimit(10);

        // Calculate total for percentage calculations
        double total = revenueByCategoryMap.values().stream().mapToDouble(Double::doubleValue).sum();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : revenueByCategoryMap.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Doanh thu theo danh mục sản phẩm");
        chart.setPrefHeight(300);

        // Add percentage labels
        addPieChartPercentages(chart, total);

        return chart;
    }

    private BarChart<String, Number> createTopProductsChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Sản phẩm");

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng bán");

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 10 sản phẩm bán chạy");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng bán ra");

        List<Map.Entry<String, Integer>> topProducts = dashboardStatistics.getTopSellingProducts(10);

        for (Map.Entry<String, Integer> entry : topProducts) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
        barChart.setPrefHeight(300);
        return barChart;
    }

    private PieChart createOrderStatusChart() {
        Map<String, Integer> orderStatusCount = dashboardStatistics.getOrderStatusCounts();

        // Calculate total for percentage calculations
        double total = orderStatusCount.values().stream().mapToDouble(Integer::doubleValue).sum();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : orderStatusCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Phân bố đơn hàng theo trạng thái");
        chart.setPrefHeight(300);

        // Add percentage labels
        addPieChartPercentages(chart, total);

        return chart;
    }

    private StackedBarChart<String, Number> createInventoryByCategoryChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Danh mục");

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng tồn kho");

        final StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        stackedBarChart.setTitle("Số lượng tồn kho theo danh mục");

        Map<String, Map<String, Integer>> inventoryData = dashboardStatistics.getInventoryByCategory();

        XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
        activeSeries.setName("Sản phẩm đang hoạt động");

        XYChart.Series<String, Number> inactiveSeries = new XYChart.Series<>();
        inactiveSeries.setName("Sản phẩm không hoạt động");

        for (Map.Entry<String, Map<String, Integer>> entry : inventoryData.entrySet()) {
            String categoryName = entry.getKey();
            Map<String, Integer> statusMap = entry.getValue();

            activeSeries.getData().add(new XYChart.Data<>(categoryName, statusMap.get("Active")));
            inactiveSeries.getData().add(new XYChart.Data<>(categoryName, statusMap.get("Inactive")));
        }

        stackedBarChart.getData().addAll(activeSeries, inactiveSeries);
        stackedBarChart.setPrefHeight(300);
        return stackedBarChart;
    }
    private LineChart<String, Number> createAverageOrderValueChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Giá trị trung bình (USD)");

        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Giá trị trung bình đơn hàng theo thời gian");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Giá trị trung bình");

        Map<String, Double> data = dashboardStatistics.getAverageOrderValueByMonth(12);

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);
        return lineChart;
    }

    private BarChart<String, Number> createProductProfitabilityChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Sản phẩm");

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Lợi nhuận (USD)");

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 10 sản phẩm có lợi nhuận cao nhất");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lợi nhuận");

        List<Map.Entry<String, Double>> data = dashboardStatistics.getProductProfitability(10);

        for (Map.Entry<String, Double> entry : data) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
        return barChart;
    }

    private PieChart createSalesBySupplierChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Double> data = dashboardStatistics.getSalesBySupplier();

        // Calculate total for percentage calculations
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Doanh thu theo nhà cung cấp");

        // Add percentage labels
        addPieChartPercentages(chart, total);

        return chart;
    }
    private void addPieChartPercentages(PieChart chart, double total) {
        chart.getData().forEach(data -> {
            double percentage = (data.getPieValue() / total) * 100;
            String text = String.format("%s (%.1f%%)", data.getName(), percentage);

            Tooltip tooltip = new Tooltip(text);
            Tooltip.install(data.getNode(), tooltip);

            data.setName(text);
        });

        chart.setLabelsVisible(true);
        chart.setLabelLineLength(10);
    }
}

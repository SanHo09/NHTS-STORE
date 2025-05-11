package com.nhom4.nhtsstore.ui.page.dashboard;

import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

/**
 * Utility class for common chart operations.
 */
public class ChartUtility {

    private final LanguageManager languageManager;

    public ChartUtility(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    /**
     * Get localized month names for charts
     */
    public String[] getLocalizedMonthNames() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            // Use i+1 since months are 1-indexed
            months[i] = languageManager.getText("dashboard.chart.month") + " " + (i + 1);
        }
        return months;
    }

    /**
     * Add percentage labels to pie chart slices
     */
    public void addPieChartPercentages(PieChart chart, double total) {
        if (total <= 0) return; // Avoid division by zero
        
        Platform.runLater(() -> {
            for (PieChart.Data data : chart.getData()) {
                try {
                    double percentage = (data.getPieValue() / total) * 100;
                    String percentText = String.format("%.1f%%", percentage);
                    String displayText = String.format("%s (%s)", data.getName(), percentText);
                    String tooltipText = String.format("%s: %,.2f (%.1f%%)",
                            data.getName(), data.getPieValue(), percentage);
    
                    data.setName(displayText);
    
                    // Create tooltip with more detailed information
                    final Node node = data.getNode();
                    if (node != null) {
                        createToolTipForNodeData(node, tooltipText);
                    }
                } catch (Exception e) {
                    // Log error but continue processing other data points
                    System.err.println("Error processing pie chart data: " + e.getMessage());
                }
            }
    
            chart.setLabelsVisible(true);
            chart.setLabelLineLength(10);
        });
    }

    /**
     * Add tooltips to bar chart data points
     */
    public void addBarChartTooltips(XYChart<String, Number> barChart) {
        addChartTooltips(barChart, (series, data) ->
                String.format("%s: %,.2f", data.getXValue(), data.getYValue().doubleValue()));
    }

    /**
     * Add tooltips to stacked bar chart data points
     */
    public void addStackedBarChartTooltips(XYChart<String, Number> barChart) {
        addChartTooltips(barChart, (series, data) ->
                String.format("%s - %s: %,.2f", data.getXValue(), series.getName(), data.getYValue().doubleValue()));
    }

    /**
     * Add tooltips to line chart data points
     */
    public void addLineChartTooltips(XYChart<String, Number> lineChart) {
        addChartTooltips(lineChart, (series, data) ->
                String.format("%s: %s - %,.2f", series.getName(), data.getXValue(), data.getYValue().doubleValue()));
    }

    /**
     * Generic method to add tooltips to chart data points
     */
    private <X, Y> void addChartTooltips(XYChart<X, Y> chart,
                                         TooltipFormatter<X, Y> tooltipFormatter) {
        Platform.runLater(() -> {
            try {
                for (XYChart.Series<X, Y> series : chart.getData()) {
                    for (XYChart.Data<X, Y> data : series.getData()) {
                        Node node = data.getNode();
                        if (node != null) {
                            String tooltipText = tooltipFormatter.format(series, data);
                            createToolTipForNodeData(node, tooltipText);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error adding chart tooltips: " + e.getMessage());
            }
        });
    }

    /**
     * Create and install a tooltip for a chart node
     */
    private void createToolTipForNodeData(Node node, String tooltipText) {
        if (node == null) return;
        
        try {
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            tooltip.setShowDelay(javafx.util.Duration.millis(100));
            tooltip.setHideDelay(javafx.util.Duration.millis(200));
            
            // Remove any previous tooltips first
            Tooltip.uninstall(node, null);
            Tooltip.install(node, tooltip);
            
            node.setOnMouseEntered(event -> node.setStyle("-fx-opacity: 0.8;"));
            node.setOnMouseExited(event -> node.setStyle("-fx-opacity: 1.0;"));
        } catch (Exception e) {
            System.err.println("Error installing tooltip: " + e.getMessage());
        }
    }

    /**
     * Function interface for formatting tooltips
     */
    @FunctionalInterface
    private interface TooltipFormatter<X, Y> {
        String format(XYChart.Series<X, Y> series, XYChart.Data<X, Y> data);
    }
} 
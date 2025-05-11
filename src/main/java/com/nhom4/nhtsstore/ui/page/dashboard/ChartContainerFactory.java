package com.nhom4.nhtsstore.ui.page.dashboard;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Supplier;

/**
 * Factory for creating chart containers with lazy loading functionality.
 */
public class ChartContainerFactory {

    /**
     * Creates a container that will lazily load a chart when it's time to display it.
     * Shows a loading spinner until the chart is ready.
     * 
     * @param title Title of the chart
     * @param chartFactory Function to create the chart
     * @return A container with the chart
     */
    public static StackPane createLazyLoadingChart(String title, Supplier<Region> chartFactory) {
        StackPane container = new StackPane();
        container.getStyleClass().add("dashboard-card");
        container.setPadding(new Insets(15));

        VBox placeholder = new VBox();
        placeholder.setAlignment(Pos.CENTER);

        Text titleText = new Text(title);
        titleText.getStyleClass().add("card-title");

        MFXProgressSpinner spinner = new MFXProgressSpinner();
        spinner.setRadius(20);

        placeholder.getChildren().addAll(titleText, spinner);
        container.getChildren().add(placeholder);

        // Each chart loads independently in its own virtual thread
        Thread.startVirtualThread(() -> {
            Region chart = null;
            try {
                // Create the chart
                chart = chartFactory.get();
                final Region finalChart = chart;
                
                if (finalChart != null) {
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        try {
                            setChartSize(finalChart, 440);
                            
                            // Clear old content and add new chart
                            VBox content = new VBox(5);
                            Text chartTitle = new Text(title);
                            chartTitle.getStyleClass().add("card-title");
                            content.getChildren().addAll(chartTitle, finalChart);
                            
                            container.getChildren().clear();
                            container.getChildren().add(content);
                        } catch (Exception e) {
                            System.err.println("Error updating chart UI: " + e.getMessage());
                            // In case of error, show error message instead of spinner
                            showErrorMessage(container, title);
                        }
                    });
                } else {
                    Platform.runLater(() -> showErrorMessage(container, title));
                }
            } catch (Exception e) {
                System.err.println("Error creating chart: " + e.getMessage());
                Platform.runLater(() -> showErrorMessage(container, title));
            }
        });

        return container;
    }
    
    /**
     * Helper method to show an error message in a chart container
     */
    private static void showErrorMessage(StackPane container, String title) {
        container.getChildren().clear();
        Text titleText = new Text(title);
        titleText.getStyleClass().add("card-title");
        Text errorText = new Text("Không thể tải dữ liệu biểu đồ");
        errorText.getStyleClass().add("error-text");
        VBox errorBox = new VBox(10);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.getChildren().addAll(titleText, errorText);
        container.getChildren().add(errorBox);
    }
    
    /**
     * Creates a container for statistic cards with lazy loading.
     * 
     * @param title Title of the card
     * @param cardFactory Function to create the card
     * @return A container with the card
     */
    public static StackPane createLazyLoadingCardContainer(String title, Supplier<Region> cardFactory) {
        StackPane container = new StackPane();
        container.getStyleClass().add("dashboard-card");

        // Create loading placeholder
        VBox placeholder = new VBox(10);
        placeholder.setPadding(new Insets(20));
        placeholder.setAlignment(Pos.CENTER);

        Text titleText = new Text(title);
        titleText.getStyleClass().add("card-title");

        MFXProgressSpinner spinner = new MFXProgressSpinner();
        spinner.setRadius(15);

        placeholder.getChildren().addAll(titleText, spinner);
        container.getChildren().add(placeholder);

        // Each card loads independently in its own virtual thread
        Thread.startVirtualThread(() -> {
            Region cardContent = null;
            try {
                // Generate the card content
                cardContent = cardFactory.get();
                final Region finalCardContent = cardContent;
                
                if (finalCardContent != null) {
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        try {
                            container.getChildren().clear();
                            container.getChildren().add(finalCardContent);
                        } catch (Exception e) {
                            System.err.println("Error updating card UI: " + e.getMessage());
                            showCardErrorMessage(container, title);
                        }
                    });
                } else {
                    Platform.runLater(() -> showCardErrorMessage(container, title));
                }
            } catch (Exception e) {
                System.err.println("Error creating card: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> showCardErrorMessage(container, title));
            }
        });

        return container;
    }
    
    /**
     * Helper method to show an error message in a card container
     */
    private static void showCardErrorMessage(StackPane container, String title) {
        VBox errorContent = new VBox(10);
        errorContent.setPadding(new Insets(20));
        errorContent.setAlignment(Pos.CENTER);

        Text errorTitle = new Text(title);
        errorTitle.getStyleClass().add("card-title");

        Text errorMessage = new Text("Không thể tải dữ liệu");
        errorMessage.getStyleClass().add("error-message");

        errorContent.getChildren().addAll(errorTitle, new Separator(), errorMessage);

        container.getChildren().clear();
        container.getChildren().add(errorContent);
    }
    
    /**
     * Sets the size of a chart for consistent display
     */
    private static void setChartSize(Region chart, double height) {
        chart.setPrefHeight(height);
        chart.setMaxHeight(height);
        chart.setMinHeight(height);
        chart.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        chart.setMaxWidth(900);
    }
} 
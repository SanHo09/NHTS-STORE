package com.nhom4.nhtsstore.ui.customComponent;

import com.nhom4.nhtsstore.ui.ViewName;
import com.nhom4.nhtsstore.utils.PanelManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BreadcrumbBar extends HBox {
    private final PanelManager panelManager;

    public BreadcrumbBar(PanelManager panelManager) {
        this.panelManager = panelManager;

        // Styling
        setSpacing(5);
        setPadding(new Insets(5));
        getStyleClass().add("breadcrumb-bar");

        // Initial update
        updateBreadcrumb();

        // Listen for navigation changes in ApplicationState
        panelManager.navigationHistoryProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::updateBreadcrumb);
        });

        panelManager.currentPositionProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::updateBreadcrumb);
        });
    }

    private void updateBreadcrumb() {
        getChildren().clear();

        List<ViewName> history = panelManager.getNavigationHistory();
        int currentPosition = panelManager.getCurrentHistoryPosition();

        for (int i = 0; i <= currentPosition; i++) {
            ViewName viewName = history.get(i);
            Button breadcrumbItem = createBreadcrumbButton(viewName, i);

            getChildren().add(breadcrumbItem);

            // Add separator if not the last item
            if (i < currentPosition) {
                Label separator = new Label(" > ");
                separator.getStyleClass().add("breadcrumb-separator");
                getChildren().add(separator);
            }
        }
    }

    private Button createBreadcrumbButton(ViewName viewName, int position) {
        Button button = new Button(formatViewName(viewName.getValue()));
        button.getStyleClass().add("breadcrumb-button");

        // Style differently if current position
        if (position == panelManager.getCurrentHistoryPosition()) {
            button.getStyleClass().add("current-breadcrumb");
        }

        button.setOnAction(event -> panelManager.navigateToBreadcrumb(position));
        return button;
    }

    private String formatViewName(String viewName) {
        // Format the view name for display (capitalize first letter, remove underscore, etc)
        return viewName.substring(0, 1).toUpperCase() + viewName.substring(1);
    }
}
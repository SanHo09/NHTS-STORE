package com.nhom4.nhtsstore.ui;

import animatefx.animation.FadeOut;
import com.nhom4.nhtsstore.NhtsStoreApplication;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LoadingDialog extends JDialog {

    private final JFXPanel fxPanel;
    private MFXProgressSpinner progressSpinner;
    private Label progressLabel;
    private VBox content;
    private StackPane root;

    public LoadingDialog(Frame owner) {
        setSize(450, 450);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        setBackground(new java.awt.Color(0, 0, 0, 0)); // Transparent background for Swing dialog

        // Initialize JavaFX
        fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new Dimension(450, 450));
        fxPanel.setOpaque(false);
        initFX();
        // Center the panel
        setLayout(new GridBagLayout());
        add(fxPanel);


    }

    private void initFX() {
        Platform.runLater(() -> {
            // Create glass background
            Rectangle glassPanel = new Rectangle(320, 420);
            glassPanel.setArcWidth(20);
            glassPanel.setArcHeight(20);
            glassPanel.setFill(Color.rgb(255, 255, 255, 0.15)); // Very light color with transparency
            glassPanel.setStroke(Color.rgb(255, 255, 255, 0.5));
            glassPanel.setStrokeWidth(1);

            // Apply blur effect for glass look
            BoxBlur blur = new BoxBlur();
            blur.setWidth(5);
            blur.setHeight(5);
            blur.setIterations(2);
            glassPanel.setEffect(blur);

            // Create logo ImageView
            ImageView logoImageView = null;
            try {
                // Load image from resources
                Image logoImage = new Image(Objects.requireNonNull(NhtsStoreApplication.class.getResourceAsStream("/NHTS_Store_logo.png")));
                logoImageView = new ImageView(logoImage);
                logoImageView.setFitHeight(150);
                logoImageView.setFitWidth(150);
                logoImageView.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Could not load logo image: " + e.getMessage());
                // Create a fallback label if image fails to load
                Label logoPlaceholder = new Label("NHTS");
                logoPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 36));
                logoPlaceholder.setTextFill(Color.WHITE);
                logoImageView = new ImageView(); // Empty ImageView to avoid null issues
            }

            // Loading spinner
            progressSpinner = new MFXProgressSpinner();
            progressSpinner.setRadius(30);
            progressSpinner.setPrefHeight(90);
            progressSpinner.setPrefWidth(90);
            progressSpinner.setStyle("-fx-accent: white;");

            // Label for progress percentage
            progressLabel = new Label("0%");
            progressLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            progressLabel.setTextFill(Color.WHITE);

            // Add app name label
            Label appNameLabel = new Label("NHTS Store");
            appNameLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
            appNameLabel.setTextFill(Color.WHITE);

            // Use VBox to align logo, spinner and label vertically
            content = new VBox(20);
            if (logoImageView.getImage() != null) {
                content.getChildren().add(logoImageView);
            } else if (!content.getChildren().isEmpty() && content.getChildren().get(0) instanceof Label) {
                content.getChildren().add(0, (Label)content.getChildren().get(0));
            }

            content.getChildren().addAll(appNameLabel, progressSpinner, progressLabel);
            content.setAlignment(Pos.CENTER);
            content.setStyle("-fx-background-color: transparent;");

            // Create stack pane with glass background and content
            root = new StackPane(glassPanel, content);
            root.setStyle("-fx-background-color: transparent;");

            Scene scene = new Scene(root, 350, 450);
            scene.setFill(Color.TRANSPARENT); // Important for transparency

            fxPanel.setScene(scene);
        });
    }

    public void setProgress(double progress) {
        Platform.runLater(() -> {
            if (progressLabel != null) {
                // Format the progress as a percentage
                int percentValue = (int) progress;
                progressLabel.setText(percentValue + "%");
            }
        });
    }

    public void fadeOut(Runnable onFinished) {
        Platform.runLater(() -> {
            if (content != null) {
                FadeOut fadeOut = new FadeOut(root);
                fadeOut.setSpeed(1.5);
                fadeOut.setOnFinished(event -> {
                    if (onFinished != null) {
                        SwingUtilities.invokeLater(onFinished);
                    }
                });
                fadeOut.play();
            } else if (onFinished != null) {
                SwingUtilities.invokeLater(onFinished);
            }
        });
    }
}
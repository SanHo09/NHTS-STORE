package com.nhom4.nhtsstore.ui.shared.components;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

public class GlobalLoadingGlassPane extends JPanel {
    private JFXPanel loadingJfxPanel;
    private MFXProgressSpinner progressSpinner = new MFXProgressSpinner();
    
    public GlobalLoadingGlassPane() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 0));

        Platform.runLater(() -> {
            // Create a new JFXPanel for JavaFX content
            loadingJfxPanel = new JFXPanel();
            loadingJfxPanel.setPreferredSize(new Dimension(200, 200));
            loadingJfxPanel.setOpaque(false);
            loadingJfxPanel.setLayout(new GridBagLayout());
            
            // Create a transparent StackPane to hold the spinner
            StackPane stackPane = new StackPane();
            stackPane.setStyle("-fx-background-color: transparent;");
            
            // Configure spinner
            progressSpinner.setStyle("-fx-background-color: transparent;");
            progressSpinner.setMaxSize(100, 100);
            
            stackPane.getChildren().add(progressSpinner);
            
            // Set the scene with transparent background
            Scene scene = new Scene(stackPane);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            loadingJfxPanel.setScene(scene);

            add(loadingJfxPanel);
        });
        
        // Block all events
        addMouseListener(new MouseAdapter() {});
        addMouseMotionListener(new MouseMotionAdapter() {});
        addKeyListener(new KeyAdapter() {});
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Semi-transparent overlay
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100)); // 40% opacity
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}

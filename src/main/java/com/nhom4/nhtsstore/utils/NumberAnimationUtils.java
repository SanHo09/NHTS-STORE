package com.nhom4.nhtsstore.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

public class NumberAnimationUtils {

    /**
     * Animates a number increasing from 0 to target value
     *
     * @param textNode The Text node to animate
     * @param targetValue The final value to reach
     * @param durationMs Animation duration in milliseconds
     * @param formatter Function to format the current value (e.g., currency format)
     */
    public static void animateNumber(Text textNode, double targetValue, int durationMs,
                                     Function<Double, String> formatter) {
        // Store original text for fallback
        String originalText = textNode.getText();

        // Create an observable property to bind the animation to
        DoubleProperty value = new SimpleDoubleProperty(0);

        // Update the text when the value changes
        value.addListener((observable, oldValue, newValue) -> {
            textNode.setText(formatter.apply(newValue.doubleValue()));
        });

        // Create the animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(value, 0)),
                new KeyFrame(Duration.millis(durationMs), new KeyValue(value, targetValue))
        );

        // Handle any errors gracefully
        timeline.setOnFinished(event -> {
            // Ensure the final text is correct
            textNode.setText(formatter.apply(targetValue));
        });

        timeline.play();
    }

    // Convenient method for currency values
    public static void animateCurrency(Text textNode, double targetValue) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of(Locale.US.getLanguage(),Locale.US.getCountry()));
        animateNumber(textNode, targetValue, 1200, currencyFormat::format);
    }

    // Convenient method for percentage values
    public static void animatePercentage(Text textNode, double targetValue) {
        animateNumber(textNode, targetValue, 1000, value -> String.format("%.1f%%", value));
    }

    // Convenient method for integer values
    public static void animateInteger(Text textNode, int targetValue) {
        animateNumber(textNode, targetValue, 800, value -> String.valueOf(value.intValue()));
    }
}
package com.nhom4.nhtsstore.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Function;
@Slf4j
public class IconUtil {
    public static BufferedImage createSvgIcon(String resourcePath, int width, int height, java.util.function.Function<Color, Color> colorFilter) {
        try {
            FlatSVGIcon svgIcon = new FlatSVGIcon(JavaFxSwing.class.getResourceAsStream(resourcePath));

            // Apply custom color filter if specified
            if (colorFilter != null) {
                svgIcon = svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(colorFilter));
            }

            // Create a derived icon with the specified dimensions
            FlatSVGIcon sizedIcon = svgIcon.derive(width, height);

            // Create buffered image
            BufferedImage bufferedImage = new BufferedImage(
                    width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sizedIcon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();

            return bufferedImage;
        } catch (IOException e) {
            log.error("Failed to load SVG: {}", resourcePath, e);
            return null;
        }
    }
    /**
     * Creates a JavaFX Image from an SVG with custom color filtering
     * @param resourcePath the path to the SVG resource
     * @param width the width of the image
     * @param height the height of the image
     * @param colorFilter custom color transformation function
     * @return a JavaFX Image created from the SVG resource
     */
    @SneakyThrows
    public static javafx.scene.image.Image createFxImageFromSvg(
            String resourcePath,
            int width,
            int height,
            java.util.function.Function<Color, Color> colorFilter) {

        return SwingFXUtils.toFXImage(createSvgIcon(resourcePath, width, height, colorFilter), null);

    }
    public static Icon createSwingIconFromSvg(
            String resourcePath,
            int width,
            int height,
            java.util.function.Function<Color, Color> colorFilter) {
        FlatSVGIcon svgIcon = null;
        try {
            svgIcon = new FlatSVGIcon(JavaFxSwing.class.getResourceAsStream(resourcePath));
            if (colorFilter != null) {
                svgIcon = svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(colorFilter));
            }
            return svgIcon.derive(width, height);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Creates a JavaFX ImageView from an SVG resource with color customization
     * @param resourcePath the path to the SVG resource
     * @param width the width of the image
     * @param height the height of the image
     * @param colorFilter custom color transformation function
     * @return a JavaFX ImageView created from the SVG resource
     */
    public static ImageView createFxImageViewFromSvg(String resourcePath, int width, int height, Function<Color, Color> colorFilter) {
        Image fxImage = createFxImageFromSvg(resourcePath, width, height, colorFilter);
        if (fxImage == null) {
            return null;
        }

        ImageView imageView = new ImageView(fxImage);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }
}

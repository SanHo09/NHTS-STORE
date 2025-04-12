package com.nhom4.nhtsstore.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for embedding JavaFX components into Swing applications
 */
@Slf4j

public class JavaFxSwing {
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

            // Convert to JavaFX image
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            log.error("Failed to load SVG: {}", resourcePath, e);
            return null;
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
    /**
     * Converts a Swing component to a JavaFX Node
     * @param swingComponent the Swing component to convert
     * @return a JavaFX SwingNode containing the Swing component
     */
    public static SwingNode toFxNode(JComponent swingComponent) {
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(swingComponent);
        });

        return swingNode;
    }
    /**
     * Creates a loading scene with a spinner
     * @return a Scene containing a centered loading spinner
     */
    private static Scene createLoadingScene() {
        StackPane loadingPane = new StackPane();
        loadingPane.setAlignment(Pos.CENTER);
        MFXProgressSpinner spinner = new MFXProgressSpinner();
        loadingPane.getChildren().add(spinner);
        return new Scene(loadingPane);
    }
    /**
     * Creates a JFXPanel with a loading spinner
     * @return a JFXPanel with a loading spinner
     */
    private static JFXPanel createLoadingJFXPanel() {
        JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(() -> jfxPanel.setScene(createLoadingScene()));
        return jfxPanel;
    }
    /**
     * Creates a JFXPanel with the given FXML loaded into it
     * @param fxmlPath the path to the FXML file
     * @param applicationContext Spring application context for controller creation
     * @return a JFXPanel with the FXML content
     */
    public static JFXPanel createJFXPanelFromFxml(String fxmlPath, ApplicationContext applicationContext) {
        JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(JavaFxSwing.class.getResource(fxmlPath));
                loader.setControllerFactory(applicationContext::getBean);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return jfxPanel;
    }

    /**
     * Creates a JFXPanel with the given JavaFX node
     * @param node the JavaFX node to display
     * @return a JFXPanel containing the node
     */
    public static JFXPanel createJFXPanel(Parent node) {
        JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            Scene scene = new Scene(node);
            jfxPanel.setScene(scene);
        });
        return jfxPanel;
    }

    /**
     * Creates a JFXPanel from FXML with access to the controller
     * @param <T> the controller type
     * @param fxmlPath the path to the FXML file
     * @param applicationContext Spring application context for controller creation
     * @param controllerConsumer a consumer that will receive the controller instance
     * @return a JFXPanel with the FXML content
     */
    public static <T> JFXPanel createJFXPanelWithController(
            String fxmlPath,
            ApplicationContext applicationContext,
            Consumer<T> controllerConsumer) {

        JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(JavaFxSwing.class.getResource(fxmlPath));
                loader.setControllerFactory(applicationContext::getBean);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);

                @SuppressWarnings("unchecked")
                T controller = (T) loader.getController();
                if (controllerConsumer != null) {
                    controllerConsumer.accept(controller);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return jfxPanel;
    }

    /**
     * Creates a JFXPanel from FXML with access to the controller and optional loading screen
     * @param <T> the controller type
     * @param fxmlPath the path to the FXML file
     * @param applicationContext Spring application context for controller creation
     * @param showLoading whether to show a loading spinner while loading the FXML
     * @param controllerConsumer a consumer that will receive the controller instance
     * @return a JFXPanel with the FXML content
     */
    public static <T> JFXPanel createJFXPanelWithController(
            String fxmlPath,
            ApplicationContext applicationContext,
            boolean showLoading,
            Consumer<T> controllerConsumer) {

        if (!showLoading) {
            return createJFXPanelWithController(fxmlPath, applicationContext, controllerConsumer);
        }

        JFXPanel jfxPanel = createLoadingJFXPanel();
        Platform.runLater(() -> {
            Thread loadThread = new Thread(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(JavaFxSwing.class.getResource(fxmlPath));
                    loader.setControllerFactory(applicationContext::getBean);
                    Parent root = loader.load();

                    Platform.runLater(() -> {
                        Scene actualScene = new Scene(root);
                        jfxPanel.setScene(actualScene);

                        @SuppressWarnings("unchecked")
                        T controller = (T) loader.getController();
                        if (controllerConsumer != null) {
                            controllerConsumer.accept(controller);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            loadThread.setDaemon(true);
            loadThread.start();
        });
        return jfxPanel;
    }

    /**
     * Creates a JFXPanel with the given FXML loaded asynchronously with a loading spinner
     * @param <T> the controller type
     * @param fxmlPath the path to the FXML file
     * @param applicationContext Spring application context for controller creation
     * @param controllerConsumer a consumer that will receive the controller instance
     * @return a JFXPanel initially showing a loading spinner, then the FXML content
     */
    public static <T> JFXPanel createJFXPanelWithControllerAndLoading(
            String fxmlPath,
            ApplicationContext applicationContext,
            Consumer<T> controllerConsumer) {

        return createJFXPanelWithController(fxmlPath, applicationContext, true, controllerConsumer);
    }
    /**
     * Runs a task on the JavaFX thread and waits for completion
     * @param runnable The task to run
     */
    public static void runAndWait(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Runs a task on the JavaFX thread and returns the result
     * @param callable The task to run
     * @return The result of the task
     */
    @SneakyThrows
    public static <T> T runAndReturn(java.util.concurrent.Callable<T> callable)
            throws ExecutionException, InterruptedException {
        if (Platform.isFxApplicationThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }

        FutureTask<T> task = new FutureTask<>(callable);
        Platform.runLater(task);
        return task.get();
    }
}

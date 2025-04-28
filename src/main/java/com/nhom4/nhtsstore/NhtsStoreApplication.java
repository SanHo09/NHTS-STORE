package com.nhom4.nhtsstore;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.ui.LoadingDialog;
import com.nhom4.nhtsstore.ui.MainFrame;
import javafx.application.Platform;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class NhtsStoreApplication {
    private static LoadingDialog loadingDialog;
    private static Timer progressAnimationTimer;
    private static int currentProgress = 0;
    private static int targetProgress = 0;

    public static void main(String[] args) {
        // First, ensure the JavaFX toolkit is initialized
        try {
            SwingUtilities.invokeAndWait(() -> {
                FlatIntelliJLaf.setup();
                new javafx.embed.swing.JFXPanel();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Create and show the loading dialog, wait for it to be fully visible
        final CountDownLatch dialogReadyLatch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            Platform.runLater(() -> {
                loadingDialog = new LoadingDialog(null);
                loadingDialog.setVisible(true);

                // Short delay to ensure dialog is fully rendered
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.millis(200));
                pause.setOnFinished(event -> dialogReadyLatch.countDown());
                pause.play();
            });
        });

        try {
            // Wait until dialog is visible before starting Spring
            dialogReadyLatch.await();

            // Initialize the progress animation timer
            setupProgressAnimationTimer();

            SecurityContextHolder.setStrategyName(
                    SecurityContextHolder.MODE_GLOBAL
            );

            new SpringApplicationBuilder(NhtsStoreApplication.class)
                    .headless(false)
                    .listeners(new ApplicationListener<ApplicationEvent>() {
                        @Override
                        public void onApplicationEvent(ApplicationEvent event) {
                            updateProgress(event);
                        }
                    })
                    .run(args);
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                if (progressAnimationTimer != null) {
                    progressAnimationTimer.stop();
                }
                if (loadingDialog != null) {
                    loadingDialog.dispose();
                }
                System.exit(1);
            });
        }
    }

    private static void setupProgressAnimationTimer() {
        progressAnimationTimer = new Timer(30, e -> {
            if (currentProgress < targetProgress) {
                // Increase by small increments (1-2% at a time)
                currentProgress = Math.min(currentProgress + 1, targetProgress);
                updateProgressBar(currentProgress);
            }
        });
        progressAnimationTimer.start();
    }

    @SneakyThrows
    private static void updateProgress(ApplicationEvent event) {
        if (loadingDialog == null) return;

        if (event instanceof ApplicationStartingEvent) {
            setTargetProgress(10);
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            setTargetProgress(30);
        } else if (event instanceof ApplicationContextInitializedEvent) {
            setTargetProgress(50);
        } else if (event instanceof ApplicationPreparedEvent) {
            setTargetProgress(70);
        } else if (event instanceof ContextRefreshedEvent) {
            setTargetProgress(90);
        } else if (event instanceof ApplicationReadyEvent) {
            setTargetProgress(100);


            ConfigurableApplicationContext context = ((ApplicationReadyEvent) event).getApplicationContext();

            // Short delay to see 100%
            new Timer(500, e -> {
                ((Timer)e.getSource()).stop();
                if (progressAnimationTimer != null) {
                    progressAnimationTimer.stop();
                }
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    loadingDialog = null;
                    MainFrame mainFrame = context.getBean(MainFrame.class);
                    mainFrame.setVisible(true);
                });
            }).start();
        }
    }

    private static void setTargetProgress(int progress) {
        targetProgress = progress;
        // If timer isn't running, start it
        if (!progressAnimationTimer.isRunning()) {
            progressAnimationTimer.start();
        }
    }

    private static void updateProgressBar(int progress) {
        SwingUtilities.invokeLater(() -> {
            if (loadingDialog != null) {
                loadingDialog.setProgress(progress);
            }
        });
    }
}
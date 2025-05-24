package com.nhom4.nhtsstore;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.nhom4.nhtsstore.ui.LoadingDialog;
import com.nhom4.nhtsstore.ui.MainFrame;
import javafx.application.Platform;
import lombok.SneakyThrows;
import com.nhom4.nhtsstore.utils.AppFont;
import java.awt.Font;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableScheduling
public class NhtsStoreApplication {
    private static LoadingDialog loadingDialog;
    private static Timer progressAnimationTimer;
    private static int currentProgress = 0;
    private static int targetProgress = 0;

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                setUIFont(AppFont.DEFAULT_FONT);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Platform.startup(() -> {
            loadingDialog = new LoadingDialog(null);
            loadingDialog.setVisible(true);

        });

        try {

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
                JOptionPane.showMessageDialog(null,
                        "An unknown error occurred while starting the application.",
                        "Error",  JOptionPane.ERROR_MESSAGE);

            });
            System.exit(1);
        }
    }

    private static void setupProgressAnimationTimer() {
        progressAnimationTimer = new Timer(20, e -> {
            if (currentProgress < targetProgress) {
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
            new Timer(500, e -> {
                ((Timer)e.getSource()).stop();
                if (progressAnimationTimer != null) {
                    progressAnimationTimer.stop();
                }
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    loadingDialog = null;
                    MainFrame mainFrame = context.getBean(MainFrame.class);
                    mainFrame.init();
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

    public static void setUIFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}
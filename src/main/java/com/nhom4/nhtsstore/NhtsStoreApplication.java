package com.nhom4.nhtsstore;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.ui.LoadingDialog;
import com.nhom4.nhtsstore.ui.MainFrame;
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

@SpringBootApplication
public class NhtsStoreApplication {
    private static LoadingDialog loadingDialog;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatIntelliJLaf.setup();
            new javafx.embed.swing.JFXPanel();
            loadingDialog = new LoadingDialog(null);
            loadingDialog.setVisible(true);
        });

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
    }
    @SneakyThrows
    private static void updateProgress(ApplicationEvent event) {
        if (loadingDialog == null) return;
        if (event instanceof ApplicationStartingEvent) {
            updateProgressBar(10);
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            updateProgressBar(30);
        } else if (event instanceof ApplicationContextInitializedEvent) {
            updateProgressBar(50);
        } else if (event instanceof ApplicationPreparedEvent) {
            updateProgressBar(70);
        } else if (event instanceof ContextRefreshedEvent) {
            updateProgressBar(90);
        } else if (event instanceof ApplicationReadyEvent) {
            ConfigurableApplicationContext context = ((ApplicationReadyEvent) event).getApplicationContext();
            MainFrame mainFrame = context.getBean(MainFrame.class);
            SwingUtilities.invokeLater(() -> {
                loadingDialog.dispose();
                loadingDialog = null;
                mainFrame.setVisible(true);
            });
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
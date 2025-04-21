package com.nhom4.nhtsstore;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.nhom4.nhtsstore.ui.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.swing.*;

@SpringBootApplication
public class NhtsStoreApplication {

    public static void main(String[] args) {
        SecurityContextHolder.setStrategyName(
                SecurityContextHolder.MODE_GLOBAL
        );
        ConfigurableApplicationContext context = new SpringApplicationBuilder(NhtsStoreApplication.class)
                .headless(false)
                .run(args);
        MainFrame mainFrame = context.getBean(MainFrame.class);
        SwingUtilities.invokeLater(() -> {
            FlatIntelliJLaf.setup();
            mainFrame.setVisible(true);
        });
    }

}

package com.nhom4.nhtsstore;


import com.nhom4.nhtsstore.ui.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class NhtsStoreApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(NhtsStoreApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.setVisible(true);
        });

    }
}

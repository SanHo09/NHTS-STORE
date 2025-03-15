package com.nhom4.nhtsstore.ui;

import jakarta.annotation.PostConstruct;


import org.springframework.stereotype.Component;
import javax.swing.*;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MainFrame extends JFrame {
    private final MainPanel mainPanel;

    @PostConstruct
    private void init() {
        setTitle("NHTS Store");
        setSize(1200,720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(mainPanel);

        
    }
}
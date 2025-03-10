package com.nhom4.nhtsstore.ui;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;



@Component
@RequiredArgsConstructor
public class MainFrame extends JFrame {
    private final MainPanel mainPanel;

    @PostConstruct
    private void init() {
        setTitle("NHTS Store");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(mainPanel);
    }
}
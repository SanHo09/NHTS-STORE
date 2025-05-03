package com.nhom4.nhtsstore.ui.shared.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

public class GlobalLoadingGlassPane extends JPanel {
    private final JLabel loadingLabel;

    public GlobalLoadingGlassPane() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        // Tạo label chứa ảnh GIF
        loadingLabel = new JLabel();

        try {
             ImageIcon gifIcon = new ImageIcon(getClass().getResource("/images/rolling-transparent.gif"));
             loadingLabel.setIcon(gifIcon);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback nếu không load được GIF
            loadingLabel.setText("Loading...");
        }
        add(loadingLabel);
        
        // Block all events
        addMouseListener(new MouseAdapter() {});
        addMouseMotionListener(new MouseMotionAdapter() {});
        addKeyListener(new KeyAdapter() {});
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Lớp xám mờ
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100)); // xám 40% opacity
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}

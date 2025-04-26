package com.nhom4.nhtsstore.ui.shared.components;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author NamDang
 */
public class ToggleSwitch extends JToggleButton {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 25;
    private static final int PADDING = 2;

    public ToggleSwitch() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addItemListener(e -> repaint());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Anti-aliasing cho mượt
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nền
        if (isSelected()) {
            g2.setColor(new Color(0x0f156d)); // Màu xanh khi bật
        } else {
            g2.setColor(new Color(0x9e9e9e)); // Màu xám khi tắt
        }
        g2.fillRoundRect(0, 0, WIDTH, HEIGHT, HEIGHT, HEIGHT);

        // Nút tròn
        g2.setColor(Color.WHITE);
        int circleDiameter = HEIGHT - PADDING * 2;
        int circleX = isSelected() ? WIDTH - circleDiameter - PADDING : PADDING;
        g2.fillOval(circleX, PADDING, circleDiameter, circleDiameter);

        g2.dispose();
    }
}

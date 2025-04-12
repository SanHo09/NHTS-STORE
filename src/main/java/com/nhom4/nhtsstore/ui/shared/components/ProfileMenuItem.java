package com.nhom4.nhtsstore.ui.shared.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProfileMenuItem extends JPanel {
    private Color normalColor = Color.WHITE;
    private Color hoverColor = new Color(245, 245, 245);

    @SneakyThrows
    public ProfileMenuItem(String text, String iconPath) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(normalColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Add icon if provided
        if (iconPath != null) {
            FlatSVGIcon icon = new FlatSVGIcon(this.getClass().getResourceAsStream(iconPath));
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(img));
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            add(iconLabel);
        }

        // Add text
        JLabel textLabel = new JLabel(text);
        add(textLabel);

        // Add empty panel to push content to left
        add(Box.createHorizontalGlue());

        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(120, 40);
    }
}
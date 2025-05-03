package com.nhom4.nhtsstore.ui.shared.components;

import com.nhom4.nhtsstore.utils.AppFont;
import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;
    private Color placeholderColor = Color.GRAY; // màu mặc định
    private int placeholderPadding = 10;          // padding mặc định
    private boolean placeholderItalic = false;    // mặc định italic
    
    public PlaceholderTextField(int columns) {
        super(columns);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        repaint();
    }

    public int getPlaceholderPadding() {
        return placeholderPadding;
    }

    public void setPlaceholderPadding(int placeholderPadding) {
        this.placeholderPadding = placeholderPadding;
        repaint();
    }

    public boolean isPlaceholderItalic() {
        return placeholderItalic;
    }

    public void setPlaceholderItalic(boolean placeholderItalic) {
        this.placeholderItalic = placeholderItalic;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder != null && getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(placeholderColor);

            if (placeholderItalic) {
                g2.setFont(AppFont.DEFAULT_FONT.deriveFont(12f).deriveFont(Font.ITALIC));
            } else {
                g2.setFont(AppFont.DEFAULT_FONT.deriveFont(12f));
            }

            FontMetrics fm = g2.getFontMetrics();
            int paddingY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, placeholderPadding, paddingY);
            g2.dispose();
        }
    }
}

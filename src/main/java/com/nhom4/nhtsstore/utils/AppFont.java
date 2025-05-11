package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.NhtsStoreApplication;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class AppFont {

    public static final Font DEFAULT_FONT;

    static {
        Font tempFont = new Font("SansSerif", Font.PLAIN, 14); // fallback
        try {
            tempFont = Font.createFont(
                    Font.TRUETYPE_FONT, 
                    NhtsStoreApplication.class.getResourceAsStream("/fonts/Vietnamese Lato/Lato Regular.ttf"))
                    .deriveFont(Font.PLAIN, 14f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        DEFAULT_FONT = tempFont;
    }

    private AppFont() {
        // Ngăn không cho new class này
    }
}

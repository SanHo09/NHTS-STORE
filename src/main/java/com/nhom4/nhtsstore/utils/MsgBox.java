package com.nhom4.nhtsstore.utils;

import javax.swing.*;
import java.awt.*;

public class MsgBox {
    public static void alert(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "NHTS-Store", 1);
    }

    public static boolean confirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "NHTS-Store", 0, 3);
        return result == 0;
    }

    public static String prompt(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message, "NHTS-Store", 1);
    }
}
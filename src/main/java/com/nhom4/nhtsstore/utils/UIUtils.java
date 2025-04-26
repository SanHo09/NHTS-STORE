package com.nhom4.nhtsstore.utils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 *
 * @author NamDang
 */
public class UIUtils {
    public static void applySelectAllOnFocus(Object... components) {
        for (Object component : components) {
            if (component instanceof JTextComponent textComponent) {
                textComponent.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        SwingUtilities.invokeLater(textComponent::selectAll);
                    }
                });
            } else if (component instanceof JSpinner spinner) {
                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
                    JTextField textField = defaultEditor.getTextField();
                    textField.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            SwingUtilities.invokeLater(textField::selectAll);
                        }
                    });
                }
            } else {
                System.err.println("Unsupported component: " + component.getClass().getName());
            }
        }
    }
}    
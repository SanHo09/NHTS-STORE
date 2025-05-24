/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom4.nhtsstore.common;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Sang
 */
public class ImageHelper {
    public static void SetLabelImage(JLabel lbl, int width, int height, byte[] imageData) {
        if (imageData == null) {
            // Set default image if no data is provided
            ImageIcon defaultIcon = new ImageIcon(ImageHelper.class.getResource("/images/No_Image_Available.jpg"));
            Image defaultImage = defaultIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            lbl.setIcon(new ImageIcon(defaultImage));
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {

                ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                BufferedImage originalImage = ImageIO.read(bis);

                int w = originalImage.getWidth();
                int h = originalImage.getHeight();

                // Calculate aspect ratio
                double ratio = Math.min((double) width / w, (double) height / h);
                int newWidth = (int) (w * ratio);
                int newHeight = (int) (h * ratio);

                // Create scaled instance
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                e.printStackTrace();
                ImageIcon defaultIcon = new ImageIcon(ImageHelper.class.getResource("/images/No_Image_Available.jpg"));
                Image defaultImage = defaultIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(defaultImage));
            }
        });
    }
}

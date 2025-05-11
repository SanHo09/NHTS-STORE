/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom4.nhtsstore.common;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Sang
 */
public class ImageHelper {
    public static void SetLabelImage(JLabel label, int labelWidth, int labelHeight, byte[] imageData) {
        try {
            BufferedImage bufferedImage;

            if (imageData == null) {
                bufferedImage = ImageIO.read(ImageHelper.class.getResourceAsStream("/images/No_Image_Available.jpg"));
            } else {
                bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            }

            Image scaledImage = bufferedImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

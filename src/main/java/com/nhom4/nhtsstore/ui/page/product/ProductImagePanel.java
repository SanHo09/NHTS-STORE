package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.ProductImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

/**
 * A panel to display product images with radio buttons for thumbnail selection
 */
public class ProductImagePanel extends JPanel {
    private List<ProductImage> images;
    private ButtonGroup thumbnailGroup;
    private JPanel imageTablePanel;
    
    public ProductImagePanel(List<ProductImage> images) {
        this.images = images;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Product Images"));
        
        // Create panel for image table
        imageTablePanel = new JPanel(new GridBagLayout());
        
        // Create header
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        JLabel imageHeaderLabel = new JLabel("Image", SwingConstants.RIGHT);
        JLabel thumbnailHeaderLabel = new JLabel("Thumbnail", SwingConstants.RIGHT);
        JLabel actionHeaderLabel = new JLabel("Action", SwingConstants.RIGHT);
        headerPanel.add(imageHeaderLabel);
        headerPanel.add(thumbnailHeaderLabel);
        headerPanel.add(actionHeaderLabel);
        
        // Set up constraints for the header
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;
        headerConstraints.gridwidth = 3;
        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        headerConstraints.weightx = 1.0;
        headerConstraints.insets = new Insets(5, 5, 5, 5);
        
        imageTablePanel.add(headerPanel, headerConstraints);
        
        // Add scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(imageTablePanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initialize with any existing images
        thumbnailGroup = new ButtonGroup();
        refreshImageList();
    }
    
    /**
     * Refresh the image list display
     */
    public void refreshImageList() {
        // Clear the thumbnail button group
        if (thumbnailGroup != null) {
            ButtonModel selectedModel = thumbnailGroup.getSelection();
            for (Enumeration<AbstractButton> buttons = thumbnailGroup.getElements(); buttons.hasMoreElements();) {
                thumbnailGroup.remove(buttons.nextElement());
            }
        }
        
        // Create a new button group
        thumbnailGroup = new ButtonGroup();
        
        // Remove all components except the header
        Component header = imageTablePanel.getComponent(0);
        imageTablePanel.removeAll();
//        imageTablePanel.add(header, getHeaderConstraints());
        
        // Re-add all images
        if (images != null && !images.isEmpty()) {
            int row = 1;
            for (ProductImage image : images) {
                addImageRow(image, row++);
            }
        } else {
            // Display a message when no images
            GridBagConstraints noImageConstraints = new GridBagConstraints();
            noImageConstraints.gridx = 0;
            noImageConstraints.gridy = 1;
            noImageConstraints.gridwidth = 3;
            noImageConstraints.fill = GridBagConstraints.HORIZONTAL;
            noImageConstraints.weightx = 1.0;
            noImageConstraints.insets = new Insets(10, 5, 5, 5);
            
            JLabel noImageLabel = new JLabel("No images uploaded yet", SwingConstants.CENTER);
            imageTablePanel.add(noImageLabel, noImageConstraints);
        }
        
        revalidate();
        repaint();
    }
    
    private GridBagConstraints getHeaderConstraints() {
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;
        headerConstraints.gridwidth = 3;
        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        headerConstraints.weightx = 1.0;
        headerConstraints.insets = new Insets(5, 5, 5, 5);
        return headerConstraints;
    }
    
    /**
     * Add an image row to the table
     */
    private void addImageRow(ProductImage image, int row) {
        // Image panel (left column)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Create image label
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Set image if available
        if (image.getImageData() != null) {
            ImageIcon icon = new ImageIcon(image.getImageData());
            // Resize the image if needed
            if (icon.getIconWidth() > 100 || icon.getIconHeight() > 100) {
                // Calculate dimensions while maintaining aspect ratio
                int width = icon.getIconWidth();
                int height = icon.getIconHeight();
                double ratio = (double) width / height;
                
                int newWidth, newHeight;
                if (width > height) {
                    newWidth = 100;
                    newHeight = (int) (100 / ratio);
                } else {
                    newHeight = 100;
                    newWidth = (int) (100 * ratio);
                }
                
                // Use SCALE_AREA_AVERAGING for better quality
                Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_AREA_AVERAGING);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setIcon(icon);
            }
        } else {
            imageLabel.setText(image.getImageName());
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JRadioButton thumbnailRadio = new JRadioButton();
        thumbnailRadio.setSelected(image.isThumbnail());
        thumbnailRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update all images in the list
                for (ProductImage img : images) {
                    img.setThumbnail(img == image);
                }
            }
        });
        
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        // radioPanel.add(Box.createVerticalGlue());
        radioPanel.add(thumbnailRadio);
        radioPanel.add(Box.createVerticalGlue());
        
        thumbnailGroup.add(thumbnailRadio);
        
        // Create a panel to center the radio button
        JPanel centerPanel = new JPanel();
        centerPanel.add(thumbnailRadio);
        radioPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Action panel (right column) with remove button
        // JPanel actionPanel = new JPanel(new BorderLayout());
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if this is the thumbnail
                boolean wasThumbnail = image.isThumbnail();
                
                // Remove from the list
                images.remove(image);
                
                // If we removed the thumbnail and there are other images, set a new one
                if (wasThumbnail && !images.isEmpty()) {
                    images.get(0).setThumbnail(true);
                }
                
                // Remove from button group
                thumbnailGroup.remove(thumbnailRadio);
                
                // Refresh the display - recreate the entire panel with updated selections
                refreshImageList();
            }
        });
        
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.add(Box.createVerticalGlue());
        actionPanel.add(removeButton);
        actionPanel.add(Box.createVerticalGlue());
        
        // Add image panel to table
        GridBagConstraints imageConstraints = new GridBagConstraints();
        imageConstraints.gridx = 0;
        imageConstraints.gridy = row;
        imageConstraints.fill = GridBagConstraints.BOTH;
        imageConstraints.weightx = 0.5;
        imageConstraints.insets = new Insets(2, 5, 2, 2);
        imageTablePanel.add(imagePanel, imageConstraints);
        
        // Add radio button panel to table
        GridBagConstraints radioConstraints = new GridBagConstraints();
        radioConstraints.gridx = 1;
        radioConstraints.gridy = row;
        radioConstraints.fill = GridBagConstraints.BOTH;
        radioConstraints.weightx = 0.25;
        radioConstraints.insets = new Insets(2, 2, 2, 2);
        imageTablePanel.add(radioPanel, radioConstraints);
        
        // Add action panel to table
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridx = 2;
        actionConstraints.gridy = row;
        actionConstraints.fill = GridBagConstraints.BOTH;
        actionConstraints.weightx = 0.25;
        actionConstraints.insets = new Insets(2, 2, 2, 5);
        imageTablePanel.add(actionPanel, actionConstraints);
    }
    
    /**
     * Update the image list
     */
    public void setImages(List<ProductImage> images) {
        this.images = images;
        refreshImageList();
    }
    
    /**
     * Get the current list of images
     */
    public List<ProductImage> getImages() {
        return images;
    }
}
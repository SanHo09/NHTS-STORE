package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.entities.ProductImage;
import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.services.CategoryService;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.ProductImageService;
import com.nhom4.nhtsstore.services.ProductService;
import com.nhom4.nhtsstore.services.SupplierService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.components.DatePicker;
import com.nhom4.nhtsstore.ui.shared.components.GlobalLoadingManager;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.TableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author NamDang
 */
@Scope("prototype")
@Component
public class ProductEditPanel extends JPanel implements RoutablePanel {
    @Autowired
    private PanelManager panelManager;
    @Autowired
    private ApplicationState applicationState;
    @Autowired
    private ProductService productService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;
    
    private Product product;
    private JPanel imagePanel;
    private JButton uploadButton;
    private List<ProductImage> images = new ArrayList<>();
    private ProductImage selectedThumbnail;
    
    public ProductEditPanel() {}

    @Override
    public void onNavigate(RouteParams params) {
        this.product = params.get("entity", Product.class);
        initForm();
    }

    private void initForm() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
//        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        int fieldWidth = 400;
        int row = 0;
        int column = 0;
        
        // Product Name
        JTextField nameField = new JTextField(product != null ? product.getName() : "");
        addFieldToForm(formPanel, createLabeledField("Product Name:", nameField, fieldWidth), gbc, column, row++);
        
        // Sale Price
        JSpinner salePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getSalePrice(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        addFieldToForm(formPanel, createLabeledField("Sale Price:", salePriceField, fieldWidth), gbc, column, row++);
        
        // Purchase Price
        JSpinner purchasePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getPurchasePrice(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        addFieldToForm(formPanel, createLabeledField("Purchase Price:", purchasePriceField, fieldWidth), gbc, column, row++);
        
        // Stock
        JSpinner quantityField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getQuantity(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        addFieldToForm(formPanel, createLabeledField("Stock:", quantityField, fieldWidth), gbc, column, row++);
        
        // Active
        ToggleSwitch activeToggle = new ToggleSwitch();
        activeToggle.setSelected(product != null && product.isActive());
        addFieldToForm(formPanel, createLabeledField("Status:", activeToggle, fieldWidth), gbc, column, row++);

        column = 1;
        row = 0;
        // Manufacturer
        JTextField manufacturerField = new JTextField(product != null ? product.getManufacturer() : "");
        addFieldToForm(formPanel, createLabeledField("Manufacturer:", manufacturerField, fieldWidth), gbc, column, row++);

        // Manufacture Date
        DatePicker manufactureDatePicker = new DatePicker();
        manufactureDatePicker.setDate(product != null && product.getManufactureDate() != null
                ? product.getManufactureDate()
                : new Date());
        addFieldToForm(formPanel, createLabeledField("Manufacture Date:", manufactureDatePicker, fieldWidth), gbc, column, row++);
        
        // Expiry Date
        DatePicker expiryDatePicker = new DatePicker();
        expiryDatePicker.setDate(product != null && product.getExpiryDate() != null
                ? product.getExpiryDate()
                : new Date());
        addFieldToForm(formPanel, createLabeledField("Expiry Date:", expiryDatePicker, fieldWidth), gbc, column, row++);
        
        // Supplier
        List<Supplier> suppliers = supplierService.findAll();
        JComboBox<Supplier> supplierCombo = new JComboBox<>(suppliers.toArray(new Supplier[0]));
        if (product != null) supplierCombo.setSelectedItem(product.getSupplier());
        addFieldToForm(formPanel, createLabeledField("Supplier:", supplierCombo, fieldWidth), gbc, column, row++);
        
        // Category
        List<Category> categories = categoryService.findAll();
        JComboBox<Category> categoryCombo = new JComboBox<>(categories.toArray(new Category[0]));
        if (product != null) categoryCombo.setSelectedItem(product.getCategory());
        addFieldToForm(formPanel, createLabeledField("Category:", categoryCombo, fieldWidth), gbc, column, row++);


        // Images
        uploadButton = new JButton("Upload");
        addFieldToForm(formPanel, createLabeledField("Images:", uploadButton, 120), gbc, column, row++);
        
        imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JScrollPane imageScrollPane = new JScrollPane(imagePanel);
//        imageScrollPane.setPreferredSize(new Dimension(300, 400));
        addFieldToForm(formPanel, createLabeledField("", imageScrollPane, 200), gbc, column, row++);

        uploadButton.addActionListener(e -> uploadImages());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton deleteButton = new JButton("Delete");
        
        // Select all value when focus
        UIUtils.applySelectAllOnFocus(nameField, salePriceField, purchasePriceField, manufacturerField, quantityField);
        
        saveButton.addActionListener(e -> {
            try {
                GlobalLoadingManager.getInstance().showSpinner();
                Product updatedProduct = product != null ? product : new Product();
                updatedProduct.setName(nameField.getText());
                updatedProduct.setSalePrice(((Number) salePriceField.getValue()).doubleValue());
                updatedProduct.setPurchasePrice(((Number) purchasePriceField.getValue()).doubleValue());
                updatedProduct.setManufacturer(manufacturerField.getText());
                updatedProduct.setManufactureDate(manufactureDatePicker.getDate());
                updatedProduct.setExpiryDate((Date) expiryDatePicker.getDate());
                updatedProduct.setSupplier((Supplier) supplierCombo.getSelectedItem());
                updatedProduct.setCategory((Category) categoryCombo.getSelectedItem());
                updatedProduct.setQuantity((Integer) quantityField.getValue());
                updatedProduct.setActive(activeToggle.isSelected());
                
                for (ProductImage img : images) {
                    img.setThumbnail(img == selectedThumbnail);
                    img.setProduct(product);
                }
                product.setImages(images);

                productService.save(updatedProduct);
                JOptionPane.showMessageDialog(this,
                        "Save successfully",
                        "Save Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                this.returnToList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error saving product: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                GlobalLoadingManager.getInstance().hideSpinner();
            }
        });

        cancelButton.addActionListener(e -> {
            this.returnToList();
        });

        deleteButton.addActionListener(e -> delete());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        if (product != null) {
            buttonPanel.add(deleteButton);
        }

        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonPanel.setBorder(null);

        add(buttonPanel);
        add(formPanel);
        
        String[][] data = {
            { "Kundan Kumar Jha", "4031", "CSE" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" },
            { "Anand Jha", "6014", "IT" }
        };
        String[] columnNames = { "Name", "Roll Number", "Department" };
        JTable table = new JTable(data, columnNames);
        add(table);
        
        revalidate();
        repaint();
    }
    
    private void returnToList() {
        JPanel listPanel = applicationState.getViewPanelByBean(ProductListPanel.class);
        panelManager.navigateTo(null, listPanel);
    }
    
    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteById(product.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete product " + product.getName() + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                this.returnToList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting product: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void addImageToPanel(ProductImage image) {
        JLabel imageLabel = new JLabel(new ImageIcon(image.getImageData()));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setPreferredSize(new Dimension(100, 100));

        // Chọn làm thumbnail khi click
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedThumbnail = image;
                for (java.awt.Component comp : imagePanel.getComponents()) {
                    comp.setBackground(null);
                }
                imageLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
        });

        // Nút xoá
        JButton deleteBtn = new JButton("X");
        deleteBtn.addActionListener(e -> {
            images.remove(image);
            imagePanel.remove(imageLabel.getParent());
            imagePanel.revalidate();
            imagePanel.repaint();
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(imageLabel, BorderLayout.CENTER);
        wrapper.add(deleteBtn, BorderLayout.NORTH);

        imagePanel.add(wrapper);
    }

    private void uploadImages() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                try {
                    byte[] data = Files.readAllBytes(file.toPath());
                    String contentType = Files.probeContentType(file.toPath());

                    ProductImage image = new ProductImage();
                    image.setImageData(data);
                    image.setImageName(file.getName());
                    image.setContentType(contentType);
                    image.setThumbnail(false); // mặc định

                    images.add(image);
                    addImageToPanel(image);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            revalidate();
            repaint();
        }
    }
    
    private void addFieldToForm(JPanel formPanel, JPanel fieldPanel, GridBagConstraints gbc, int column, int row) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 40);
        formPanel.add(fieldPanel, gbc);
    }

    private JPanel createLabeledField(String labelText, JComponent field, int width) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Không có khoảng cách
        field.setPreferredSize(new Dimension(width, field.getPreferredSize().height));
        rightPanel.add(field);

        panel.add(label, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }
}

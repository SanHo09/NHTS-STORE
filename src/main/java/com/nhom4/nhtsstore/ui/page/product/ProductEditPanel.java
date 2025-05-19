package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.entities.ProductImage;
import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.services.impl.*;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.components.DatePicker;
import com.nhom4.nhtsstore.ui.shared.components.ToggleSwitch;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nhom4.nhtsstore.viewmodel.product.ProductInfo;
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
    @Autowired
    private BarcodeScannerService barcodeScannerService;
    @Autowired
    private FindProductInformationService findProductInformationService;
    private Product product;
    private JButton uploadButton;
    private List<ProductImage> images = new ArrayList<>();
    private ProductImagePanel imagePanel;

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

        int fieldWidth = 300;
        int row = 0;
        int column = 0;

        // Product Name
        JTextField nameField = new JTextField(product != null ? product.getName() : "");
        addFieldToForm(formPanel, createLabeledField("Product Name:", nameField, fieldWidth), gbc, column, row++);
        JTextField barcodeField= new JTextField(product != null ? product.getBarcode() : "");
        addFieldToForm(formPanel, createLabeledField("Barcode",barcodeField,fieldWidth), gbc, column, row++);
        // Sale Price
        JSpinner salePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getSalePrice().doubleValue(), 0.0, 1000.0, 1.0)
                            : new SpinnerNumberModel(0.0, 0.0, 1000.0, 1.0));
        addFieldToForm(formPanel, createLabeledField("Sale Price:", salePriceField, fieldWidth), gbc, column, row++);

        // Purchase Price
        JSpinner purchasePriceField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getPurchasePrice().doubleValue(), 0.0, 1000.0, 1.0)
                            : new SpinnerNumberModel(0.0, 0.0, 1000.0, 1.0));
        addFieldToForm(formPanel, createLabeledField("Purchase Price:", purchasePriceField, fieldWidth), gbc, column, row++);

        // Stock
        JSpinner quantityField = new JSpinner(product != null 
                            ? new SpinnerNumberModel(product.getQuantity(), 0, 1000, 1)
                            : new SpinnerNumberModel(0, 0, 1000, 1));
        addFieldToForm(formPanel, createLabeledField("Stock:", quantityField, fieldWidth), gbc, column, row++);

        // Active
        ToggleSwitch activeToggle = new ToggleSwitch();
        activeToggle.setSelected(product != null && product.isActive());
        addFieldToForm(formPanel, createLabeledField("Visible:", activeToggle, fieldWidth), gbc, column, row++);

//        // Cột Spacer
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(Box.createHorizontalStrut(100), gbc);

        column = 2;
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
        uploadButton = new JButton("Upload images");
        uploadButton.addActionListener(e -> uploadImages());
        addFieldToForm(formPanel, createLabeledField("", uploadButton, 140), gbc, column, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
//        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
//        saveButton.setBackground(new Color(0x355F9F));
//        saveButton.setForeground(Color.WHITE);
//        saveButton.setOpaque(true);
//        saveButton.setBorderPainted(false);

        JButton cancelButton = new JButton("Cancel");

        JButton deleteButton = new JButton("Delete");

        // Select all value when focus
        UIUtils.applySelectAllOnFocus(nameField, salePriceField, purchasePriceField, manufacturerField, quantityField);

        saveButton.addActionListener(e -> {
            try {
                Product updatedProduct = product != null ? product : new Product();
                updatedProduct.setName(nameField.getText());
                updatedProduct.setBarcode(barcodeField.getText());
                updatedProduct.setSalePrice(BigDecimal.valueOf(((Number) salePriceField.getValue()).doubleValue()));
                updatedProduct.setPurchasePrice(BigDecimal.valueOf(((Number) purchasePriceField.getValue()).doubleValue()));
                updatedProduct.setManufacturer(manufacturerField.getText());
                updatedProduct.setManufactureDate(manufactureDatePicker.getDate());
                updatedProduct.setExpiryDate((Date) expiryDatePicker.getDate());
                updatedProduct.setSupplier((Supplier) supplierCombo.getSelectedItem());
                updatedProduct.setCategory((Category) categoryCombo.getSelectedItem());
                updatedProduct.setQuantity((Integer) quantityField.getValue());
                updatedProduct.setActive(activeToggle.isSelected());

                for (ProductImage img : images) {
                    img.setProduct(updatedProduct);
                }
                updatedProduct.setImages(images);

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
            }
        });

        cancelButton.addActionListener(e -> {
            this.returnToList();
        });

        deleteButton.addActionListener(e -> delete());

        buttonPanel.add(saveButton);
        if (product != null) {
            buttonPanel.add(deleteButton);
        }
        buttonPanel.add(cancelButton);

        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonPanel.setBorder(null);

        imagePanel = new ProductImagePanel(images);
        JPanel imageTableContainer = new JPanel(new BorderLayout());
        imageTableContainer.add(imagePanel, BorderLayout.CENTER);

        // Load existing images if editing an existing product
        if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
            images = new ArrayList<>(product.getImages());
            imagePanel.setImages(images);
        }

        // GridBagConstraints for image panel
        GridBagConstraints imageTableGbc = new GridBagConstraints();
        imageTableGbc.gridx = 0;
        imageTableGbc.gridy = row++;
        imageTableGbc.gridwidth = 3;
        imageTableGbc.fill = GridBagConstraints.BOTH;
        imageTableGbc.weightx = 1.0;
        imageTableGbc.weighty = 1.0;
        imageTableGbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(imageTableContainer, imageTableGbc);

        add(buttonPanel);
        add(formPanel);

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
                        "Delete product " + "'" + product.getName() + "'" + " successfully",
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
                    image.setThumbnail(images.isEmpty());

                    images.add(image);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            imagePanel.setImages(images);
            revalidate();
            repaint();
        }
    }

    private void addFieldToForm(JPanel formPanel, JPanel fieldPanel, GridBagConstraints gbc, int column, int row) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(10, 0, 10, 40);
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(fieldPanel, gbc);
    }

    private JPanel createLabeledField(String labelText, JComponent field, int width) {
        JPanel panel = new JPanel(new BorderLayout(10, 0)); // Giảm khoảng cách giữa label và field xuống 10px
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height)); // Đặt chiều rộng cố định cho label

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Không có khoảng cách
        field.setPreferredSize(new Dimension(width, field.getPreferredSize().height));
        rightPanel.add(field);

        panel.add(label, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

}

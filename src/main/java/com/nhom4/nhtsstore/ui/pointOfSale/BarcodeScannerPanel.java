package com.nhom4.nhtsstore.ui.pointOfSale;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.impl.BarcodeScannerService;
import com.nhom4.nhtsstore.services.impl.ProductService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.utils.UIUtils;
import com.nhom4.nhtsstore.viewmodel.cart.CartItemVm;
import com.nhom4.nhtsstore.viewmodel.cart.CartVm;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import raven.modal.Toast;
import raven.modal.toast.option.ToastLocation;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@org.springframework.stereotype.Component
@Scope("prototype")
public class BarcodeScannerPanel extends JPanel implements RoutablePanel {
    private final NavigationService navigationService;
    private final ProductService productService;
    private final ApplicationState applicationState;
    private final BarcodeScannerService barcodeScannerService;
    private CartVm cart;

    private JTextField searchField;
    private JList<Product> searchResultsList;
    private DefaultListModel<Product> searchResultsModel;
    private JPanel productsPanel;
    private JPanel headerPanel;
    private List<Product> selectedProducts = new ArrayList<>();
    private final Map<Long, Integer> productQuantities = new HashMap<>();

    public BarcodeScannerPanel(NavigationService navigationService, ProductService productService,
                               ApplicationState applicationState, BarcodeScannerService barcodeScannerService) {
        this.navigationService = navigationService;
        this.productService = productService;
        this.applicationState = applicationState;
        this.barcodeScannerService = barcodeScannerService;
        initComponents();
    }

    @PostConstruct
    public void init() {
        cart = applicationState.getCart();

    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top navigation panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel backButton = new JLabel("←");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                navigationService.navigateTo(PointOfSalePanel.class, new RouteParams());
            }
        });

        JLabel titleLabel = new JLabel("Product Finder", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Search section
        JPanel searchSection = new JPanel(new BorderLayout(0, 10));
        searchSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Search bar panel with text field
        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        JLabel searchLabel = new JLabel("Product Name/Barcode: ");
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBarPanel.add(searchLabel, BorderLayout.WEST);
        searchBarPanel.add(searchField, BorderLayout.CENTER);

        // Scan buttons panel (horizontal layout)
        JPanel scanButtonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        scanButtonsPanel.setBorder(BorderFactory.createTitledBorder("Scan Barcode"));
        JButton scanButton = new JButton("Scan By WebCam");
        scanButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scanButton.addActionListener(e -> barcodeScannerService.scanWithWebcam(this::handleBarcodeDetected));
        scanButtonsPanel.add(scanButton);

        JButton phoneButton = new JButton("Scan By Phone");
        phoneButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneButton.addActionListener(e -> barcodeScannerService.scanWithPhoneCamera(this::handleBarcodeDetected));
        scanButtonsPanel.add(phoneButton);

        // Add scan buttons to search bar panel
        searchBarPanel.add(scanButtonsPanel, BorderLayout.EAST);

        // Search results list
        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setCellRenderer(new ProductListCellRenderer());
        searchResultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Product selected = searchResultsList.getSelectedValue();
                    if (selected != null) {
                        addToSelectedProducts(selected);
                    }
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchProducts(); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchProducts(); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchProducts(); }
        });

        JScrollPane resultsScrollPane = new JScrollPane(searchResultsList);
        resultsScrollPane.setPreferredSize(new Dimension(600, 150));

        searchSection.add(searchBarPanel, BorderLayout.NORTH);
        searchSection.add(resultsScrollPane, BorderLayout.CENTER);
        contentPanel.add(searchSection);

        // Selected products section
        JPanel productsSection = new JPanel(new BorderLayout());
        productsSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Selected Products",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        // Create header panel with column labels
        headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new GridLayout(1, 4, 10, 0));

        JLabel productNameHeader = new JLabel("Product Name");
        productNameHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel priceHeader = new JLabel("Price");
        priceHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel quantityHeader = new JLabel("Quantity");
        quantityHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel actionsHeader = new JLabel("Actions");
        actionsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));

        headerPanel.add(productNameHeader);
        headerPanel.add(priceHeader);
        headerPanel.add(quantityHeader);
        headerPanel.add(actionsHeader);

        // Create products panel
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));

        JScrollPane productsScrollPane = new JScrollPane(productsPanel);
        productsScrollPane.setPreferredSize(new Dimension(600, 300));

        productsSection.add(headerPanel, BorderLayout.NORTH);
        productsSection.add(productsScrollPane, BorderLayout.CENTER);
        contentPanel.add(productsSection);

        // Bottom cart action buttons panel
        JPanel cartButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addToCartButton = new JButton("Add All to Cart");
        addToCartButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addToCartButton.addActionListener(e -> addAllToCart());
        cartButtonsPanel.add(addToCartButton);

        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewCartButton.addActionListener(e -> navigationService.navigateTo(AppView.CART, new RouteParams()));
        cartButtonsPanel.add(viewCartButton);

        // Add content panel to main layout
        add(contentPanel, BorderLayout.CENTER);
        add(cartButtonsPanel, BorderLayout.SOUTH);
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            searchResultsModel.clear();
            return;
        }

        SwingWorker<Page<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected Page<Product> doInBackground() {
                return productService.searchWhereIsActive(keyword, List.of("name", "barcode"),
                        PageRequest.of(0, 10));
            }

            @Override
            protected void done() {
                try {
                    Page<Product> products = get();
                    searchResultsModel.clear();
                    for (Product product : products) {
                        searchResultsModel.addElement(product);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(BarcodeScannerPanel.this,
                            "Error searching products: " + ex.getMessage(),
                            "Search Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void addToSelectedProducts(Product product) {
        // Avoid duplicates
        if (selectedProducts.stream().anyMatch(p -> p.getId().equals(product.getId()))) {
            Toast.show(this, Toast.Type.WARNING,
                    "Product already in list", ToastLocation.TOP_CENTER);
            return;
        }
        selectedProducts.add(product);
        refreshSelectedProductsList();
    }

    private void refreshSelectedProductsList() {
        // Save current spinner values before refreshing
        for (Component row : productsPanel.getComponents()) {
            if (row instanceof JPanel) {
                JPanel productRow = (JPanel) row;
                Component[] components = productRow.getComponents();

                if (components.length >= 3) {
                    JPanel quantityPanel = (JPanel) components[2];
                    for (Component comp : quantityPanel.getComponents()) {
                        if (comp instanceof JSpinner) {
                            JSpinner spinner = (JSpinner) comp;
                            int index = productsPanel.getComponentZOrder(row) / 2;
                            if (index < selectedProducts.size()) {
                                Product product = selectedProducts.get(index);
                                productQuantities.put(product.getId(), (Integer) spinner.getValue());
                            }
                        }
                    }
                }
            }
        }

        productsPanel.removeAll();

        for (Product product : selectedProducts) {
            JPanel productRow = new JPanel();
            productRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            productRow.setLayout(new GridLayout(1, 4, 10, 0));
            productRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Product name cell
            JLabel nameLabel = new JLabel(product.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Price cell
            JLabel priceLabel = new JLabel(product.getSalePrice() + "$");
            priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Quantity cell with spinner - use saved value if available
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            // Get the saved quantity or default to 1
            int initialValue = productQuantities.getOrDefault(product.getId(), 1);
            JSpinner quantitySpinner = new JSpinner(
                    new SpinnerNumberModel(initialValue, 1, product.getQuantity(), 1));
            quantitySpinner.setPreferredSize(new Dimension(70, 30));
            UIUtils.applySelectAllOnFocus(quantitySpinner);

            quantityPanel.add(quantitySpinner);

            // Actions cell
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(e -> {
                selectedProducts.remove(product);
                productQuantities.remove(product.getId());  // Also remove from our map
                refreshSelectedProductsList();
            });
            actionsPanel.add(removeButton);

            // Add cells to row
            productRow.add(nameLabel);
            productRow.add(priceLabel);
            productRow.add(quantityPanel);
            productRow.add(actionsPanel);

            // Add row to panel
            productsPanel.add(productRow);
            productsPanel.add(Box.createVerticalStrut(5));
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void handleBarcodeDetected(String barcode) {
        Product product = productService.findByBarcode(barcode);
        if (product != null) {
            addToSelectedProducts(product);
            Toast.show(this, Toast.Type.SUCCESS, "Product found: " + product.getName(), ToastLocation.TOP_CENTER);
        } else {
            Toast.show(this, Toast.Type.ERROR, "Product not found with barcode: " + barcode, ToastLocation.TOP_CENTER);
        }
    }
    private void addAllToCart() {
        if (selectedProducts.isEmpty()) {
            Toast.show(this, Toast.Type.ERROR,
                    "No products selected", ToastLocation.TOP_CENTER);
            return;
        }

        // Get quantities from spinners
        for (Component row : productsPanel.getComponents()) {
            if (row instanceof JPanel) {
                JPanel productRow = (JPanel) row;
                Component[] components = productRow.getComponents();

                if (components.length >= 3) {
                    JPanel quantityPanel = (JPanel) components[2];

                    for (Component comp : quantityPanel.getComponents()) {
                        if (comp instanceof JSpinner) {
                            JSpinner spinner = (JSpinner) comp;
                            int quantity = (int) spinner.getValue();

                            int index = productsPanel.getComponentZOrder(row) / 2;
                            if (index < selectedProducts.size()) {
                                Product product = selectedProducts.get(index);
                                addProductToCart(product, quantity);
                            }
                        }
                    }
                }
            }
        }

        Toast.show(this, Toast.Type.SUCCESS,
                "Products added to cart", ToastLocation.TOP_CENTER);
        selectedProducts.clear();
        refreshSelectedProductsList();
    }

    private void addProductToCart(Product product, int quantity) {
        CartItemVm newItem = CartItemVm.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(quantity)
                .price(product.getSalePrice())
                .cost(product.getPurchasePrice())
                .manufacturer(product.getManufacturer())
                .addedDate(new Date())
                .build();

        // Update existing item or add new one
        boolean itemUpdated = false;
        for (CartItemVm item : cart.getItems()) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                itemUpdated = true;
                break;
            }
        }

        if (!itemUpdated) {
            cart.getItems().add(newItem);
        }

        // Update cart totals
        cart.setTotalAmount(calculateTotal());
        cart.setLastModifiedDate(new Date());
        applicationState.setCart(cart);
    }

    private BigDecimal calculateTotal() {
        return cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void onNavigate(RouteParams params) {
        cart = applicationState.getCart();
        searchField.setText("");
        selectedProducts.clear();
        refreshSelectedProductsList();
    }

    // Custom renderer for product list items
    private class ProductListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            Component c = super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof Product) {
                Product product = (Product) value;
                setText(product.getName() + " - " + product.getSalePrice() + "$ (Stock: " +
                        product.getQuantity() + ")");
            }

            return c;
        }
    }
}
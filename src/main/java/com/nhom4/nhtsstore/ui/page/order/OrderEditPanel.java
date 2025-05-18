package com.nhom4.nhtsstore.ui.page.order;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.enums.DeliveryStatus;
import com.nhom4.nhtsstore.enums.FulfilmentMethod;
import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.services.impl.CustomerService;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.impl.OrderService;
import com.nhom4.nhtsstore.services.impl.ProductService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class OrderEditPanel extends JPanel implements RoutablePanel {
    @Autowired
    private PanelManager panelManager;
    @Autowired
    private ApplicationState applicationState;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;

    private Order order;
    private JComboBox<Customer> customerCombo;
    private JComboBox<DeliveryStatus> statusCombo;
    private JComboBox<PaymentStatus> paymentStatusCombo;
    private JComboBox<PaymentMethod> paymentMethodCombo;
    private JComboBox<FulfilmentMethod> fulfilmentMethodCombo;

    private JSpinner totalAmountField;
    private OrderDetailTableModel orderDetailTableModel;
    private JTable orderDetailTable;
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private List<Product> products;

    public OrderEditPanel() {}

    @Override
    public void onNavigate(RouteParams params) {
        this.order = params.get("entity", Order.class);
        initForm();
    }

    private void initForm() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        int fieldWidth = 400;
        int row = 0;
        int column = 0;
        
        List<Customer> customers = customerService.findAll();
        customerCombo = new JComboBox<>(customers.toArray(new Customer[0]));
        
        List<DeliveryStatus> statusList = new ArrayList<>(Arrays.asList(DeliveryStatus.values()));
        statusCombo = new JComboBox<>(statusList.toArray(new DeliveryStatus[0]));

        List<PaymentMethod> paymentMethods = new ArrayList<>(Arrays.asList(PaymentMethod.values()));
        paymentMethodCombo = new JComboBox<>(paymentMethods.toArray(new PaymentMethod[0]));

        List<PaymentStatus> paymentStatuses = new ArrayList<>(Arrays.asList(PaymentStatus.values()));
        paymentStatusCombo = new JComboBox<>(paymentStatuses.toArray(new PaymentStatus[0]));

        List<FulfilmentMethod> fulfilmentMethods = new ArrayList<>(Arrays.asList(FulfilmentMethod.values()));
        fulfilmentMethodCombo = new JComboBox<>(fulfilmentMethods.toArray(new FulfilmentMethod[0]));

        totalAmountField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 1.0));
        totalAmountField.setEnabled(false);

        // Load products for the combo box
        products = productService.findAll();

        // Initialize order details list
        if (order != null) {
            customerCombo.setSelectedItem(order.getCustomer());
            statusCombo.setSelectedItem(order.getDeliveryStatus());
            totalAmountField.setValue(order.getTotalAmount());
            paymentMethodCombo.setSelectedItem(order.getPaymentMethod());
            paymentStatusCombo.setSelectedItem(order.getPaymentStatus());
            fulfilmentMethodCombo.setSelectedItem(order.getFulfilmentMethod());

            if (order.getOrderDetails() != null) {
                orderDetails = new ArrayList<>(order.getOrderDetails());
            }
        }
        
        // Add main form fields
        addFieldToForm(formPanel, createLabeledField("Customer:", customerCombo, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Status:", statusCombo, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Total amount:", totalAmountField, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Payment Method:", paymentMethodCombo, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Payment Status:", paymentStatusCombo, fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Create Date:", new JLabel(new Date().toString()), fieldWidth), gbc, column, row++);
        addFieldToForm(formPanel, createLabeledField("Fulfillment Method:", fulfilmentMethodCombo, fieldWidth), gbc, column, row++);

        // Create order detail table
        orderDetailTableModel = new OrderDetailTableModel();
        orderDetailTable = new JTable(orderDetailTableModel);
        orderDetailTable.setRowHeight(30);
        
        // Set up custom cell editors and renderers
        orderDetailTable.getColumnModel().getColumn(0).setCellRenderer(new OrderDetailTableCellRenderer.ProductCellEditor(products));
        orderDetailTable.getColumnModel().getColumn(0).setCellEditor(new OrderDetailTableCellRenderer.ProductCellEditor(products));
        
        orderDetailTable.getColumnModel().getColumn(1).setCellRenderer(new OrderDetailTableCellRenderer.QuantityCellEditor());
        orderDetailTable.getColumnModel().getColumn(1).setCellEditor(new OrderDetailTableCellRenderer.QuantityCellEditor());
        
        orderDetailTable.getColumnModel().getColumn(2).setCellRenderer(new OrderDetailTableCellRenderer.RemoveButtonCellEditor(rowIndex -> removeOrderDetail(rowIndex)));
        orderDetailTable.getColumnModel().getColumn(2).setCellEditor(new OrderDetailTableCellRenderer.RemoveButtonCellEditor(rowIndex -> removeOrderDetail(rowIndex)));
        
        // Set up table column widths
        orderDetailTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Product column
        orderDetailTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Quantity column
        orderDetailTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Remove button column
        
        JScrollPane tableScrollPane = new JScrollPane(orderDetailTable);
        tableScrollPane.setPreferredSize(new Dimension(fieldWidth, 200));
        
        // Create "New" button for adding rows
        JButton newDetailButton = new JButton("New");
        newDetailButton.addActionListener(e -> addNewOrderDetail());
        
        JPanel orderDetailPanel = new JPanel(new BorderLayout());
        JPanel orderDetailHeaderPanel = new JPanel(new BorderLayout());
        orderDetailHeaderPanel.add(new JLabel("Order Details:"), BorderLayout.WEST);
        orderDetailHeaderPanel.add(newDetailButton, BorderLayout.EAST);
        
        orderDetailPanel.add(orderDetailHeaderPanel, BorderLayout.NORTH);
        orderDetailPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Add order detail panel to form
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(orderDetailPanel, gbc);

        // Reset constraints
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton deleteButton = new JButton("Delete");
        
        saveButton.addActionListener(e -> save());

        cancelButton.addActionListener(e -> {
            this.returnToList();
        });

        deleteButton.addActionListener(e -> delete());

        buttonPanel.add(saveButton);
        if (order != null) {
            buttonPanel.add(deleteButton);
        }
        buttonPanel.add(cancelButton);

        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonPanel.setBorder(null);

        add(buttonPanel);
        add(formPanel);
        
        revalidate();
        repaint();
    }

    private void addNewOrderDetail() {
        if (products == null || products.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No products available to add to order",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        OrderDetail newDetail = new OrderDetail();
        newDetail.setProduct(products.get(0)); // Default to first product
        newDetail.setQuantity(1);
        newDetail.setOrder(order);
        
        orderDetails.add(newDetail);
        orderDetailTableModel.fireTableDataChanged();
        updateTotalAmount();
    }
    
    private void removeOrderDetail(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < orderDetails.size()) {
            orderDetails.remove(rowIndex);
            orderDetailTableModel.fireTableDataChanged();
            updateTotalAmount();
        }
    }
    
    private void updateTotalAmount() {
        double total =0;
        for (OrderDetail detail : orderDetails) {
            total += detail.getProduct().getSalePrice().multiply(new BigDecimal(detail.getQuantity())).doubleValue();
        }
        totalAmountField.setValue(total);
    }

    private void returnToList() {
        JPanel listPanel = applicationState.getViewPanelByBean(OrderListPanel.class);
        panelManager.navigateTo(null, listPanel);
    }

    private void save() {
        try {
            Order updatedOrder = order != null ? order : new Order();
            updatedOrder.setCustomer((Customer) customerCombo.getSelectedItem());
            updatedOrder.setDeliveryStatus((DeliveryStatus) statusCombo.getSelectedItem());
            updatedOrder.setTotalAmount(BigDecimal.valueOf(((Number) totalAmountField.getValue()).doubleValue()));
            updatedOrder.setPaymentMethod((PaymentMethod) paymentMethodCombo.getSelectedItem());
            updatedOrder.setPaymentStatus((PaymentStatus) paymentStatusCombo.getSelectedItem());
            updatedOrder.setFulfilmentMethod((FulfilmentMethod) fulfilmentMethodCombo.getSelectedItem());

            for (OrderDetail od: orderDetails) {
                od.setOrder(updatedOrder);
            }
            updatedOrder.setOrderDetails(orderDetails);

            orderService.save(updatedOrder);
            JOptionPane.showMessageDialog(this,
                    "Save successfully",
                    "Save Success", JOptionPane.INFORMATION_MESSAGE);
            EventBus.postReload(true);
            this.returnToList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving order: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void delete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this order?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderService.deleteById(order.getId());
                JOptionPane.showMessageDialog(this,
                        "Delete product " + "'" + order.getId() + "'" + " successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                EventBus.postReload(true);
                this.returnToList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting order: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void addFieldToForm(JPanel formPanel, JPanel fieldPanel, GridBagConstraints gbc, int column, int row) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(fieldPanel, gbc);
    }

    private JPanel createLabeledField(String labelText, JComponent field, int width) {
        JPanel panel = new JPanel(new BorderLayout(10, 0)); // Giảm khoảng cách giữa label và field xuống 10px
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(140, label.getPreferredSize().height)); // Đặt chiều rộng cố định cho label
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Không có khoảng cách
        field.setPreferredSize(new Dimension(width, field.getPreferredSize().height));
        rightPanel.add(field);

        panel.add(label, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }
    
    // Table model for order details
    private class OrderDetailTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Product", "Quantity", "Actions"};
        
        @Override
        public int getRowCount() {
            return orderDetails.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return JComboBox.class;
                case 1: return JSpinner.class;
                case 2: return JButton.class;
                default: return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OrderDetail detail = orderDetails.get(rowIndex);
            switch (columnIndex) {
                case 0: return detail.getProduct();
                case 1: return detail.getQuantity();
                case 2: return "Remove";
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= orderDetails.size()) {
                return; // Safeguard: do nothing if the row no longer exists
            }

            OrderDetail detail = orderDetails.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    if (aValue instanceof Product) {
                        detail.setProduct((Product) aValue);
                        updateTotalAmount();
                    }
                    break;
                case 1:
                    if (aValue instanceof Integer) {
                        detail.setQuantity((Integer) aValue);
                        updateTotalAmount();
                    }
                    break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
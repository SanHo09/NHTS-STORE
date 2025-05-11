package com.nhom4.nhtsstore.ui.page.order;

import com.nhom4.nhtsstore.entities.Product;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class OrderTablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Product> productList;

    public OrderTablePanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{ "Product", "Quantity", "Action" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Product.class;
                    case 1 -> Integer.class;
                    default -> Object.class;
                };
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);

        // Column 0: JComboBox
        JComboBox<Product> comboBox = new JComboBox<>(productList.toArray(new Product[0]));
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboBox));

        // Column 1: Spinner Editor
        table.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor());

        // Column 2: Button Renderer + Editor
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("New");
        addButton.addActionListener(e -> {
            tableModel.addRow(new Object[]{productList.get(0), 1, "Remove"});
        });

        add(addButton, BorderLayout.NORTH);
    }

    // ========== Custom Spinner Editor ==========
    static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            spinner.setValue(value != null ? value : 1);
            return spinner;
        }
    }

    // ========== Button Renderer ==========
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Remove");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // ========== Button Editor ==========
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        final JButton button = new JButton("Remove");
        int row;

        public ButtonEditor() {
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Remove";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tableModel.removeRow(row);
            fireEditingStopped();
        }
    }
}
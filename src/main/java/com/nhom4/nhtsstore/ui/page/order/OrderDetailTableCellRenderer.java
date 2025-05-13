package com.nhom4.nhtsstore.ui.page.order;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.utils.UIUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Custom renderer and editor for OrderDetail table cells
 */
public class OrderDetailTableCellRenderer {
    
    /**
     * Product ComboBox renderer and editor
     */
    public static class ProductCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        private final JComboBox<Product> productCombo;
        private final List<Product> products;
        
        public ProductCellEditor(List<Product> products) {
            this.products = products;
            this.productCombo = new JComboBox<>(new DefaultComboBoxModel<>(products.toArray(new Product[0])));
            
            this.productCombo.addActionListener(e -> {
                if (!productCombo.isPopupVisible()) {
                    stopCellEditing();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Product) {
                productCombo.setSelectedItem(value);
            } else {
                productCombo.setSelectedIndex(0);
            }
            return productCombo;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Product) {
                productCombo.setSelectedItem(value);
            } else {
                productCombo.setSelectedIndex(0);
            }
            return productCombo;
        }
        
        @Override
        public Object getCellEditorValue() {
            return productCombo.getSelectedItem();
        }
        
        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }
    
    /**
     * Quantity spinner renderer and editor
     */
    public static class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        private final JSpinner spinner;
        
        public QuantityCellEditor() {
            this.spinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            
            this.spinner.addChangeListener(e -> {
                // We don't auto-stop editing to allow the user to continue adjusting the value
            });
            
            UIUtils.applySelectAllOnFocus(spinner);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Integer) {
                spinner.setValue(value);
            } else {
                spinner.setValue(1);
            }
            return spinner;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Integer) {
                spinner.setValue(value);
            } else {
                spinner.setValue(1);
            }
            return spinner;
        }
        
        @Override
        public Object getCellEditorValue() {
            return ((Number)spinner.getValue()).intValue();
        }
        
        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }
    
    /**
     * Remove button renderer and editor
     */
    public static class RemoveButtonCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        private final JButton button;
        private int currentRow;
        private final RemoveButtonListener listener;
        
        public interface RemoveButtonListener {
            void onRemoveButtonClicked(int row);
        }
        
        public RemoveButtonCellEditor(RemoveButtonListener listener) {
            this.listener = listener;
            this.button = new JButton("Remove");
            
            this.button.addActionListener(e -> {
                if (listener != null) {
                    listener.onRemoveButtonClicked(currentRow);
                }
                fireEditingStopped();
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            return button;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Remove";
        }
    }
}
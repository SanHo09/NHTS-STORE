package com.nhom4.nhtsstore.ui.base;

import com.nhom4.nhtsstore.entities.GenericEntity;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.nhom4.nhtsstore.ui.PanelManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Component bảng dữ liệu tổng quát có thể tái sử dụng cho nhiều màn hình
 * @param <T> Loại entity mà bảng sẽ hiển thị
 */
public class GenericTablePanel<T extends GenericEntity> extends JPanel {
    private JTable table;
    private GenericTableModel<T> tableModel;
    private JPopupMenu tableRowMenu;
    private JPopupMenu headerMenu;
    private final GenericService<T> service;
    private JButton newButton;
    private final Class<T> entityClass;
    private final Class<? extends JPanel> editPanelClass;
    private final String[] columnNames;
    private final String panelTitle;
    @Autowired
    private PanelManager panelManager;
    @Autowired
    private ApplicationState applicationState;
    
    /**
     * Constructor cho GenericTablePanel
     * @param service Service cung cấp dữ liệu và thao tác với database
     * @param entityClass Class của entity
     * @param columnNames Tên các cột sẽ hiển thị
     * @param panelTitle Tiêu đề của panel
     */
    public GenericTablePanel (
        GenericService<T> service,
        Class<T> entityClass,
        Class<? extends JPanel> editPanelClass,
        String[] columnNames,
        String panelTitle) 
    {
        this.service = service;
        this.entityClass = entityClass;
        this.editPanelClass = editPanelClass;
        this.columnNames = columnNames;
        this.panelTitle = panelTitle;
        
        initComponents();
        loadData();
    }
    
    /**
     * Khởi tạo các component UI
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel tiêu đề và nút
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(panelTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        // Title nằm bên trái header
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Panel chứa các nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Nút "New" để thêm record mới
        newButton = new JButton("New");
        newButton.addActionListener(e -> addEntity());
        buttonPanel.add(newButton);
        
        // Nút menu 3 chấm
        JButton menuButton = new JButton("⋮");
        menuButton.addActionListener(e -> {
            headerMenu = new JPopupMenu();
            
            JMenuItem refreshItem = new JMenuItem("Refresh");
            refreshItem.addActionListener(ev -> loadData());
            
            JMenuItem deleteItem = new JMenuItem("Delete Selected");
            deleteItem.addActionListener(ev -> deleteSelectedRecords());
            
            headerMenu.add(refreshItem);
            headerMenu.add(deleteItem);
            
            // Hiển thị menu tại vị trí của nút
            headerMenu.show(menuButton, 0, menuButton.getHeight());
        });
        buttonPanel.add(menuButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Tạo bảng và model
        tableModel = new GenericTableModel<>(columnNames);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowHeight(30);
        table.setSurrendersFocusOnKeystroke(true);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);

        // Cấu hình cột checkbox đầu tiên
        TableColumn checkboxColumn = table.getColumnModel().getColumn(0);
        checkboxColumn.setMaxWidth(50);
        checkboxColumn.setCellRenderer(new CheckBoxRenderer());
        checkboxColumn.setCellEditor(new CheckBoxEditor());
        
        // Thiết lập checkbox cho header cột đầu tiên
        JCheckBox selectAllCheckbox = new JCheckBox();
        selectAllCheckbox.addActionListener(e -> {
            boolean selected = selectAllCheckbox.isSelected();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(selected, i, 0);
            }
            tableModel.fireTableDataChanged();
        });
        checkboxColumn.setHeaderRenderer(new CheckBoxHeaderRenderer(selectAllCheckbox));
        
        // Cấu hình cột action menu (cột cuối)
        int lastColumnIndex = tableModel.getColumnCount() - 1;
        TableColumn actionColumn = table.getColumnModel().getColumn(lastColumnIndex);
        actionColumn.setMaxWidth(30);
        actionColumn.setCellRenderer(new ActionButtonRenderer());
        actionColumn.setCellEditor(new ActionButtonEditor());
        
        // Scroll pane cho bảng
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Tải dữ liệu từ service và cập nhật bảng
     */
    public void loadData() {
        SwingWorker<List<T>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<T> doInBackground() {
                return service.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<T> data = get();
                    tableModel.setData(data);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GenericTablePanel.this,
                            "Error loading data: " + ex.getMessage(),
                            "Data Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Hiển thị dialog để tạo entity mới
     */
    private void addEntity() {
        EventBus.postEntity(null);
        JPanel newPanel = applicationState.getViewPanelByBean(editPanelClass);
        this.panelManager.navigateTo(null, newPanel);
//        // Tạo frame mới để thêm/sửa entity
//        JFrame newEntityFrame = new JFrame("Add New " + entityClass.getSimpleName());
//        newEntityFrame.setSize(500, 400);
//        newEntityFrame.setLocationRelativeTo(this);
//        
//        // Thêm form panel tương ứng với loại entity
//        try {
//            // Ở đây cần implement EntityFormPanel cho mỗi loại entity
//            // Giả sử chúng ta có một factory để tạo ra form panel phù hợp
//            JPanel formPanel = EntityFormPanelFactory.createFormPanel(entityClass, null, entity -> {
//                service.save((T) entity);
//                loadData();
//                newEntityFrame.dispose();
//            });
//            
//            newEntityFrame.add(formPanel);
//            newEntityFrame.setVisible(true);
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, 
//                    "Could not create form for " + entityClass.getSimpleName() + ": " + e.getMessage(),
//                    "Form Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
    }
    
    /**
     * Xóa các record đã chọn (có tick checkbox)
     */
    private void deleteSelectedRecords() {
        List<T> selectedEntities = tableModel.getSelectedEntities();
        
        if (selectedEntities.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No records selected for deletion",
                    "Delete", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Hiển thị dialog xác nhận
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selectedEntities.size() + " selected records?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteMany(selectedEntities);
                JOptionPane.showMessageDialog(this,
                        selectedEntities.size() + " records deleted successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting records: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Model dữ liệu cho bảng
     */
    private class GenericTableModel<E extends GenericEntity> extends AbstractTableModel {
        private final String[] columnNames;
        private List<E> data = new ArrayList<>();
        
        public GenericTableModel(String[] columnNames) {
            // Thêm cột checkbox và cột action vào mảng columnNames
            String[] enhancedColumns = new String[columnNames.length + 2];
            enhancedColumns[0] = ""; // Cột checkbox
            System.arraycopy(columnNames, 0, enhancedColumns, 1, columnNames.length);
            enhancedColumns[enhancedColumns.length - 1] = ""; // Cột action
            
            this.columnNames = enhancedColumns;
        }
        
        public void setData(List<E> data) {
            this.data = data;
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return data.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
            return Object.class;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Cho phép edit cột checkbox và cột action
            return columnIndex == 0 || columnIndex == getColumnCount() - 1;
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= data.size()) {
                return null;
            }
            
            E entity = data.get(rowIndex);
            
            if (columnIndex == 0) {
                // Cột checkbox
                return entity.isSelected();
            } else if (columnIndex == getColumnCount() - 1) {
                // Cột action
                return "⋮";
            } else {
                // Các cột dữ liệu
                try {
                    return entity.getFieldValueByIndex(columnIndex - 1);
                } catch (Exception e) {
                    return "Error";
                }
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= data.size()) {
                return;
            }
            
            E entity = data.get(rowIndex);
            
            if (columnIndex == 0 && value instanceof Boolean) {
                // Cập nhật trạng thái selected cho entity
                entity.setSelected((Boolean) value);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
        
        public E getEntityAt(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < data.size()) {
                return data.get(rowIndex);
            }
            return null;
        }
        
        public List<E> getSelectedEntities() {
            return data.stream()
                    .filter(GenericEntity::isSelected)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Renderer cho ô checkbox
     */
    private static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(JCheckBox.CENTER);
            setBorderPainted(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            if (value instanceof Boolean) {
                setSelected((Boolean) value);
            }
            
            return this;
        }
    }
    
    /**
     * Editor cho ô checkbox
     */
    private static class CheckBoxEditor extends DefaultCellEditor {
        public CheckBoxEditor() {
            super(new JCheckBox());
            JCheckBox checkBox = (JCheckBox) getComponent();
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        }
    }
    
    /**
     * Renderer cho checkbox trong header
     */
    private static class CheckBoxHeaderRenderer implements TableCellRenderer {
        private final JCheckBox selectAll;
        
        public CheckBoxHeaderRenderer(JCheckBox selectAll) {
            this.selectAll = selectAll;
            selectAll.setHorizontalAlignment(JCheckBox.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            return selectAll;
        }
    }
    
    /**
     * Renderer cho nút action
     */
    private static class ActionButtonRenderer extends JButton implements TableCellRenderer {
        public ActionButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
            setMargin(new Insets(0, 0, 0, 0));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setText("⋮");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
            return this;
        }
    }
    
    /**
     * Editor cho nút action (3 dấu chấm)
     */
    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private int currentRow;

        public ActionButtonEditor() {
            button = new JButton("⋮");
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Ngăn JTable xử lý sự kiện click trước
                    e.consume();

                    // Hiển thị menu ngay lập tức
                    showMenuForCurrentRow();

                    // Dừng edit mode
                    fireEditingStopped();
                }
            });
        }

        private void showMenuForCurrentRow() {
            T entity = tableModel.getEntityAt(currentRow);
            showRowActionMenu(button, entity, currentRow);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                  boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "⋮";
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            // Kích hoạt editor ngay khi di chuột vào
            return true;
        }
    }
    
    /**
     * Hiển thị menu cho dấu 3 chấm trong hàng
     */
    private void showRowActionMenu(JComponent component, T entity, int row) {
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> editEntity(entity));
        
        JMenuItem activateItem = new JMenuItem("Activate");
        activateItem.addActionListener(e -> activateEntity(entity));
        
        JMenuItem deactivateItem = new JMenuItem("Deactivate");
        deactivateItem.addActionListener(e -> deactivateEntity(entity));
        
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteEntity(entity));
        
        popup.add(editItem);
        popup.add(activateItem);
        popup.add(deactivateItem);
        popup.add(deleteItem);
        
        popup.show(component, 0, component.getHeight());
    }
    
    /**
     * Mở dialog chỉnh sửa entity
     */
    private void editEntity(T entity) {
        EventBus.postEntity(entity);
        JPanel editPanel = applicationState.getViewPanelByBean(editPanelClass);
        this.panelManager.navigateTo(null, editPanel);
//        JFrame editFrame = new JFrame("Edit " + entityClass.getSimpleName());
//        editFrame.setSize(500, 400);
//        editFrame.setLocationRelativeTo(this);
//        
//        try {
//            JPanel formPanel = EntityFormPanelFactory.createFormPanel(entityClass, entity, updatedEntity -> {
//                service.save((T) updatedEntity);
//                loadData();
//                editFrame.dispose();
//            });
//            
//            editFrame.add(formPanel);
//            editFrame.setVisible(true);
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, 
//                    "Could not create edit form: " + e.getMessage(),
//                    "Edit Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
    }
    
    /**
     * Kích hoạt entity
     */
    private void activateEntity(T entity) {
        try {
            entity.setActive(true);
            service.save(entity);
            loadData();
            JOptionPane.showMessageDialog(this, 
                    entityClass.getSimpleName() + " activated successfully",
                    "Activation Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error activating " + entityClass.getSimpleName() + ": " + e.getMessage(),
                    "Activation Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Hủy kích hoạt entity
     */
    private void deactivateEntity(T entity) {
        try {
            entity.setActive(false);
            service.save(entity);
            loadData();
            JOptionPane.showMessageDialog(this, 
                    entityClass.getSimpleName() + " deactivated successfully",
                    "Deactivation Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error deactivating " + entityClass.getSimpleName() + ": " + e.getMessage(),
                    "Deactivation Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa entity
     */
    private void deleteEntity(T entity) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this " + entityClass.getSimpleName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteById(entity.getId());
                loadData();
                JOptionPane.showMessageDialog(this, 
                        entityClass.getSimpleName() + " deleted successfully",
                        "Delete Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting " + entityClass.getSimpleName() + ": " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

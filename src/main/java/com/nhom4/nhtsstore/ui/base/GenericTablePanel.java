package com.nhom4.nhtsstore.ui.base;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.nhom4.nhtsstore.entities.GenericEntity;
import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.repositories.GenericRepository;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.PanelManager;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.components.GlobalLoadingManager;
import com.nhom4.nhtsstore.ui.shared.components.PlaceholderTextField;
import com.nhom4.nhtsstore.utils.AppFont;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Component bảng dữ liệu tổng quát có thể tái sử dụng cho nhiều màn hình
 * @param <T> Loại entity mà bảng sẽ hiển thị
 */
public class GenericTablePanel<T extends GenericEntity> extends JPanel implements LanguageManager.LanguageChangeListener {
    @Autowired
    private PanelManager panelManager;
    @Autowired
    private ApplicationState applicationState;
    @Autowired
    private NavigationService navigationService;
    @Autowired
    private LanguageManager languageManager;
    @Getter
    protected JTable table;
    private GenericTableModel<T> tableModel;
    private JPopupMenu tableRowMenu;
    private JPopupMenu headerMenu;
    private final GenericService service;
    private JButton newButton;
    private final Class<T> entityClass;
    private final Class<? extends JPanel> editPanelClass;
    private final Class<? extends JPanel> createPanelClass;
    private final Class<? extends GenericEditDialog> editDialogClass;
    private GenericEditDialog editDialog;
    private final String[] columnNames;
    private String panelTitle;
    private String panelTitleKey; // Key for localization
    private JTextField searchField;
    private String placeHolderMessage;
    private String placeHolderMessageKey; // Key for localization
    private JComboBox<Integer> pageSizeCombo;
    private JLabel pageInfoLabel;
    private JButton firstPageBtn, prevPageBtn, nextPageBtn, lastPageBtn;
    private JTextField pageNumberField;
    private Timer searchTimer;
    private Timer pageNumberTimer;
    private int currentPage = 0;
    private int totalPages = 0;
    private int pageSize = 10;
    private List<String> searchFields;
    private Long totalItems;
    private JLabel totalRecords;
    private JLabel titleLabel;

    /**
     * Constructor cho GenericTablePanel
     * @param service Service cung cấp dữ liệu và thao tác với database
     * @param entityClass Class của entity
     * @param columnNames Tên các cột sẽ hiển thị
     * @param panelTitle Tiêu đề của panel
     */
    public GenericTablePanel (
            GenericService service,
            Class<T> entityClass,
            Class<? extends JPanel> editPanelClass, Class<? extends JPanel> createPanelClass,
            Class<? extends GenericEditDialog> editDialogClass,
            String[] columnNames,
            String panelTitle,
            List<String> searchFields,
            String placeHolderMessage)
    {
        this.service = service;
        this.entityClass = entityClass;
        this.editPanelClass = editPanelClass;
        this.createPanelClass = createPanelClass;
        this.editDialogClass = editDialogClass;
        this.columnNames = columnNames;
        this.panelTitle = panelTitle;
        // Derive localization keys from the panel title
        this.panelTitleKey = "panel." + panelTitle.toLowerCase().replace(" ", "");
        this.placeHolderMessage = placeHolderMessage;
        this.placeHolderMessageKey = "search." + panelTitle.toLowerCase().replace(" ", "");
        this.searchFields = searchFields;
        
        initComponents();
        initPaginationComponents();
        loadData();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        languageManager.addLanguageChangeListener(this);
        updateTexts(); // Initial text update
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        languageManager.removeLanguageChangeListener(this);
    }
    
    @Override
    public void onLanguageChanged() {
        SwingUtilities.invokeLater(this::updateTexts);
    }
    
    /**
     * Update all translatable texts in the panel
     */
    protected void updateTexts() {
        if (languageManager == null) return;
        
        // Update panel title
        String localizedTitle = languageManager.getText(panelTitleKey);
        if (!localizedTitle.equals(panelTitleKey)) {
            // Only update if a translation was found
            panelTitle = localizedTitle;
        }
        
        if (titleLabel != null) {
            titleLabel.setText(panelTitle);
        }
        
        // Update placeholder text
        String localizedPlaceholder = languageManager.getText(placeHolderMessageKey);
        if (!localizedPlaceholder.equals(placeHolderMessageKey)) {
            // Only update if a translation was found
            placeHolderMessage = localizedPlaceholder;
        }
        if (searchField instanceof PlaceholderTextField) {
            ((PlaceholderTextField) searchField).setPlaceholder(placeHolderMessage);
        }
        
        // Update button texts
        if (newButton != null) {
            newButton.setText(languageManager.getText("button.new"));
        }
        
        // Update pagination controls
        updatePaginationTexts();
        
        // Force refresh of table column headers
        if (table != null && tableModel != null) {
            // Get the current localized renderer if it exists
            LocalizedHeaderRenderer localizedRenderer = null;
            if (table.getColumnCount() > 1) {
                localizedRenderer = (LocalizedHeaderRenderer) 
                    table.getColumnModel().getColumn(1).getHeaderRenderer();
            }
            
            // This triggers a complete header redraw
            tableModel.fireTableStructureChanged();
            
            // Re-apply special renderers and column configurations
            if (table.getColumnCount() > 0) {
                // Restore checkbox column
                TableColumn checkboxColumn = table.getColumnModel().getColumn(0);
                checkboxColumn.setMaxWidth(50);
                checkboxColumn.setCellRenderer(new CheckBoxRenderer());
                checkboxColumn.setCellEditor(new CheckBoxEditor());
                checkboxColumn.setHeaderRenderer(new CheckBoxHeaderRenderer(table, 0));
                
                // Reapply localized renderers
                if (localizedRenderer != null) {
                    int lastColumnIndex = table.getColumnModel().getColumnCount() - 1;
                    for (int i = 1; i < lastColumnIndex; i++) {
                        table.getColumnModel().getColumn(i).setHeaderRenderer(localizedRenderer);
                    }
                }
                
                // Restore action column
                if (table.getColumnCount() > 1) {
                    int lastColumnIndex = table.getColumnModel().getColumnCount() - 1;
                    TableColumn actionColumn = table.getColumnModel().getColumn(lastColumnIndex);
                    actionColumn.setMaxWidth(30);
                    actionColumn.setCellRenderer(new ActionButtonRenderer());
                    actionColumn.setCellEditor(new ActionButtonEditor());
                }
            }
            
            // Request repaint
            table.getTableHeader().repaint();
        }
    }
    
    /**
     * Update texts in pagination controls
     */
    private void updatePaginationTexts() {
        if (languageManager == null) return;
        if (pageInfoLabel != null && totalItems != null) {
            int start = currentPage * pageSize + 1;
            int end = Math.min((currentPage + 1) * pageSize, totalItems.intValue());
            String message = String.format(
                languageManager.getText("pagination.info"),
                currentPage + 1, totalPages, start, end, totalItems
            );
            pageInfoLabel.setText(message);
        }
        if (firstPageBtn != null) {
            firstPageBtn.setToolTipText(languageManager.getText("pagination.first"));
        }
        if (prevPageBtn != null) {
            prevPageBtn.setToolTipText(languageManager.getText("pagination.prev"));
        }
        if (nextPageBtn != null) {
            nextPageBtn.setToolTipText(languageManager.getText("pagination.next"));
        }
        if (lastPageBtn != null) {
            lastPageBtn.setToolTipText(languageManager.getText("pagination.last"));
        }
        if (totalRecords != null && totalItems != null) {
            String message = String.format(
                languageManager.getText("pagination.totalRecords"),
                totalItems
            );
            totalRecords.setText(message);
        }
    }
    
    /**
     * Khởi tạo các component UI
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel tiêu đề và nút
        JPanel titlePanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel(panelTitle);
        titleLabel.setFont(AppFont.DEFAULT_FONT.deriveFont(24f));
        // Title nằm bên trái header
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // add padding
        
        // Panel chứa các nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Nút "New" để thêm record mới
        newButton = new JButton(languageManager != null ? languageManager.getText("button.new") : "New");
        newButton.addActionListener(e -> addEntity());
        if (!Invoice.class.equals(this.entityClass)) {
            buttonPanel.add(newButton);
        }
        
        // Nút menu 3 chấm
        JButton menuButton = new JButton(new FlatSVGIcon("icons/ThreeDotsVertical.svg", 1.1f));
        menuButton.addActionListener(e -> {
            headerMenu = new JPopupMenu();
            
            JMenuItem refreshItem = new JMenuItem(languageManager != null ? 
                languageManager.getText("menu.refresh") : "Refresh");
            refreshItem.addActionListener(ev -> {
                GlobalLoadingManager.getInstance().showSpinner();
                loadData();
            });
            
            JMenuItem deleteItem = new JMenuItem(languageManager != null ? 
                languageManager.getText("menu.delete") : "Delete");
            deleteItem.addActionListener(ev -> deleteSelectedRecords());
            
            headerMenu.add(refreshItem);
            if (!Invoice.class.equals(this.entityClass)) {
                headerMenu.add(deleteItem);
            }
            
            // Hiển thị menu tại vị trí của nút
            headerMenu.show(menuButton, 0, menuButton.getHeight());
        });
        buttonPanel.add(menuButton);
                
        // Panel header
        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        searchField = new PlaceholderTextField(20);
        searchField.setPreferredSize(new Dimension(100, 25));

        PlaceholderTextField placeholderField = (PlaceholderTextField) searchField;
        placeholderField.setPlaceholder(placeHolderMessage);
        placeholderField.setPlaceholderColor(Color.LIGHT_GRAY);
        placeholderField.setPlaceholderPadding(5); // padding kể từ lề trái
        placeholderField.setPlaceholderItalic(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { debounceSearch(); }
            public void removeUpdate(DocumentEvent e) { debounceSearch(); }
            public void insertUpdate(DocumentEvent e) { debounceSearch(); }
        });
        
        // Cấu hình cho searchField
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        headerPanel.add(searchField, gbc);
        
        // Thêm một panel trống để tạo khoảng cách
        JPanel emptyPanel = new JPanel();
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Chiếm toàn bộ không gian thừa
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(emptyPanel, gbc);
        
        // Cấu hình cho buttonPanel
        gbc.gridx = 2;
        gbc.weightx = 0; // Không chiếm không gian thừa
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        headerPanel.add(buttonPanel, gbc);
        
        JPanel headerWrapperPanel = new JPanel(new BorderLayout());
        headerWrapperPanel.add(titlePanel, BorderLayout.NORTH);
        headerWrapperPanel.add(headerPanel, BorderLayout.SOUTH);
        
        add(headerWrapperPanel, BorderLayout.NORTH);
        
        // Tạo bảng và model
        tableModel = new GenericTableModel<>(columnNames);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowHeight(30);
        table.setSurrendersFocusOnKeystroke(true);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        table.setFont(AppFont.DEFAULT_FONT);
        table.getTableHeader().setFont(AppFont.DEFAULT_FONT);
        
        // Create custom header renderer for data columns
        LocalizedHeaderRenderer localizedRenderer = new LocalizedHeaderRenderer();

        // Cấu hình cột checkbox đầu tiên
        TableColumn checkboxColumn = table.getColumnModel().getColumn(0);
        checkboxColumn.setMaxWidth(50);
        checkboxColumn.setCellRenderer(new CheckBoxRenderer());
        checkboxColumn.setCellEditor(new CheckBoxEditor());

        // Thiết lập checkbox cho header cột đầu tiên
        checkboxColumn.setHeaderRenderer(new CheckBoxHeaderRenderer(table, 0));
        
        // Cấu hình cột action menu (cột cuối)
        int lastColumnIndex = tableModel.getColumnCount() - 1;
        TableColumn actionColumn = table.getColumnModel().getColumn(lastColumnIndex);
        actionColumn.setMaxWidth(30);
        actionColumn.setCellRenderer(new ActionButtonRenderer());
        actionColumn.setCellEditor(new ActionButtonEditor());
        
        // Set localized renderer for all data columns
        for (int i = 1; i < lastColumnIndex; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(localizedRenderer);
        }
        
        if (Invoice.class.equals(this.entityClass)) {
            table.getColumnModel().removeColumn(table.getColumnModel().getColumn(lastColumnIndex));
            table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
        }
        
        // Scroll pane cho bảng
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Tạo hiệu ứng sọc cho các dòng
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String cellValue = value != null ? value.toString() : "";

                // Màu theo nội dung
                if (cellValue.equalsIgnoreCase("Visible")) {
                    c.setForeground(new Color(0x28a745)); // xanh lá
                } else if (cellValue.equalsIgnoreCase("Hidden")) {
                    c.setForeground(new Color(0x6c757d)); // xám
                } else if (cellValue.equalsIgnoreCase("In stock")) {
                    c.setForeground(new Color(0x007bff)); // xanh dương
                } else if (cellValue.equalsIgnoreCase("Out of stock")) {
                    c.setForeground(new Color(0xdc3545)); // đỏ
                } else {
                    c.setForeground(Color.BLACK);
                }

                // Đặt màu nền xen kẽ
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }

               setBorder(BorderFactory.createCompoundBorder(
                   BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
                   BorderFactory.createEmptyBorder(5, 10, 5, 10)
               ));

                // Giữ nguyên màu khi được chọn
                return c;
            }
        });
    }
    
    private void initPaginationComponents() {
        // Panel phân trang
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Nút trang đầu
        firstPageBtn = new JButton(new FlatSVGIcon("icons/SkipStartFill.svg", 1.1f));
        firstPageBtn.addActionListener(e -> goToPage(0));
        
        // Nút trang trước
        prevPageBtn = new JButton(new FlatSVGIcon("icons/CaretLeftFill.svg", 1.1f));
        prevPageBtn.addActionListener(e -> goToPage(currentPage - 1));
        
        // Nút trang sau
        nextPageBtn = new JButton(new FlatSVGIcon("icons/CaretRightFill.svg", 1.1f));
        nextPageBtn.addActionListener(e -> goToPage(currentPage + 1));
        
        // Nút trang cuối
        lastPageBtn = new JButton(new FlatSVGIcon("icons/SkipEndFill.svg", 1.1f));
        lastPageBtn.addActionListener(e -> goToPage(totalPages - 1));
        
        // Hiển thị thông tin trang
        pageInfoLabel = new JLabel();
        
        // Nhập số trang
        pageNumberField = new JTextField(3);
        pageNumberField.getDocument().addDocumentListener(new DocumentListener() {
            private String lastValue = "";

            @Override
            public void insertUpdate(DocumentEvent e) {
                handlePageNumberChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handlePageNumberChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handlePageNumberChange();
            }

            private void handlePageNumberChange() {
                String currentValue = pageNumberField.getText();
                if (!currentValue.equals(lastValue)) {
                    lastValue = currentValue;
                    debouncePageNumber();
                }
            }
        });
        
        // Combo chọn số lượng item mỗi trang
        pageSizeCombo = new JComboBox<>(new Integer[]{5, 10, 20, 50, 100});
        pageSizeCombo.setSelectedItem(pageSize);
        pageSizeCombo.addActionListener(e -> {
            pageSize = (Integer) pageSizeCombo.getSelectedItem();
            currentPage = 0;
            loadData();
        });
        
        paginationPanel.add(new JLabel("Go to:"));
        paginationPanel.add(pageNumberField);
        paginationPanel.add(firstPageBtn);
        paginationPanel.add(prevPageBtn);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(nextPageBtn);
        paginationPanel.add(lastPageBtn);
        paginationPanel.add(pageSizeCombo);

        totalRecords = new JLabel();
        updateTotalRecordsLabel();
    
        // Thêm các panel vào layout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalRecords, BorderLayout.WEST);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Timer cho search debounce
        searchTimer = new Timer(500, e -> {
            searchTimer.stop();
            currentPage = 0;
            loadData();
        });
        searchTimer.setRepeats(false);
        
        // Timer cho page number debounce
        pageNumberTimer = new Timer(500, e -> {
            pageNumberTimer.stop();
            try {
                int page = Integer.parseInt(pageNumberField.getText()) - 1;
                if (page >= 0 && page < totalPages) {
                    goToPage(page);
                }
            } catch (NumberFormatException ex) {
                // Ignore invalid input
            }
        });
        pageNumberTimer.setRepeats(false);
    }
    
    protected void configureColumnWidths(int[] columnWidths) {
        TableColumnModel columnModel = table.getColumnModel();
        LocalizedHeaderRenderer localizedRenderer = (LocalizedHeaderRenderer) 
            table.getColumnModel().getColumn(1).getHeaderRenderer();
        
        for (int i = 0; i < columnModel.getColumnCount() && i < columnWidths.length; i++) {
            TableColumn column = columnModel.getColumn(i);
            int width = columnWidths[i];
            column.setPreferredWidth(width);
            
            // Cố định width cho cột checkbox và action
            if (i == 0 || i == columnModel.getColumnCount() - 1) {
                column.setMinWidth(width);
                column.setMaxWidth(width);
            } else {
                column.setMinWidth(width / 2);
                column.setMaxWidth(width * 2);
                
                // Ensure the localized renderer is set on data columns
                if (localizedRenderer != null) {
                    column.setHeaderRenderer(localizedRenderer);
                }
            }
        }
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    protected void setHeaderAlignment(int alignment) {
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Thiết lập các thuộc tính hiển thị
                setText(value != null ? value.toString() : ""); // Đảm bảo chữ không bị mất
                setBackground(new Color(240, 240, 240)); // Màu nền header
                setForeground(Color.BLACK); // Màu chữ
                setFont(AppFont.DEFAULT_FONT); // Sử dụng font chung của ứng dụng
                setHorizontalAlignment(alignment); // Căn chỉnh theo tham số

                // Thiết lập border và padding
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                setOpaque(true); // Quan trọng: phải có để hiển thị background

                return this;
            }
        });
    }

    private void updateTotalRecordsLabel() {
        if (totalRecords != null && totalItems != null) {
            String message = languageManager != null ?
                String.format(languageManager.getText("pagination.totalRecords"), totalItems) :
                String.format("Total: %d records", totalItems);
            totalRecords.setText(message);
        }
    }
    
    private void debounceSearch() {
        searchTimer.restart();
    }
    
    private void debouncePageNumber() {
        if (pageNumberTimer != null) {
            pageNumberTimer.stop();
        }

        pageNumberTimer = new Timer(500, e -> {
            try {
                String text = pageNumberField.getText().trim();
                if (!text.isEmpty()) {
                    int page = Integer.parseInt(text) - 1;
                    if (page >= 0 && page < totalPages && page != currentPage) {
                        goToPage(page);
                    }
                }
            } catch (NumberFormatException ex) {
                // Khôi phục giá trị hợp lệ nếu nhập không phải số
                SwingUtilities.invokeLater(() -> 
                    pageNumberField.setText(String.valueOf(currentPage + 1)));
            }
        });
        pageNumberTimer.setRepeats(false);
        pageNumberTimer.start();
    }
    
    private void goToPage(int page) {
        if (page >= 0 && page < totalPages && page != currentPage) {
            currentPage = page;
            loadData();
        }
    }
    
   public void loadData() {


       Thread.startVirtualThread(() -> {
           try {

               // Background processing
               String keyword = searchField.getText().trim();
               Pageable pageable = PageRequest.of(currentPage, pageSize);
               Page<T> result;

               if (keyword.isEmpty()) {
                   result = service.findAll(pageable);
               } else {
                   result = service.search(keyword, searchFields, pageable);
               }
               totalItems = result.getTotalElements();

               // Update UI on EDT
               SwingUtilities.invokeLater(() -> {
                   try {
                       tableModel.setData(result.getContent());
                       totalPages = result.getTotalPages();

                       updateTotalRecordsLabel();
                       updatePaginationControls();
                   } catch (Exception ex) {
                       JOptionPane.showMessageDialog(GenericTablePanel.this,
                               "Error loading data: " + ex.getMessage(),
                               "Data Error", JOptionPane.ERROR_MESSAGE);
                       ex.printStackTrace();
                   } finally {
                       Timer loadingTimer = new Timer(1000, e -> {
                           GlobalLoadingManager.getInstance().hideSpinner();
                       });
                       loadingTimer.setRepeats(false);
                       loadingTimer.start();
                   }
               });
           } catch (Exception ex) {
               SwingUtilities.invokeLater(() -> {
                   JOptionPane.showMessageDialog(GenericTablePanel.this,
                           "Error loading data: " + ex.getMessage(),
                           "Data Error", JOptionPane.ERROR_MESSAGE);
                   ex.printStackTrace();
                   GlobalLoadingManager.getInstance().hideSpinner();
               });
           }
       });
   }
    
    private void updatePaginationControls() {
        boolean hasPrev = currentPage > 0;
        boolean hasNext = currentPage < totalPages - 1;
        
        firstPageBtn.setEnabled(hasPrev);
        prevPageBtn.setEnabled(hasPrev);
        nextPageBtn.setEnabled(hasNext);
        lastPageBtn.setEnabled(hasNext);
        
        if (pageInfoLabel != null && totalItems != null) {
            updatePaginationTexts();
        }
        
        pageNumberField.setText(String.valueOf(currentPage + 1));
    }
    
    /**
     * Hiển thị dialog để tạo entity mới
     */
    private void addEntity() {
        if (editDialogClass != null) {
            try {
//                GenericEditDialog editDialog = editDialogClass.getDeclaredConstructor().newInstance();
                GenericEditDialog editDialog = applicationState.applicationContext.getBean(editDialogClass);
                this.editDialog = editDialog;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create dialog", e);
            }
            editDialog.showDialog(null);
        } else {
            RouteParams params = new RouteParams();
            this.navigationService.navigateTo(createPanelClass, params);
        }
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
        private final Map<Integer, String> columnKeyMap; // Maps column indices to i18n keys
        private List<E> data = new ArrayList<>();
        
        public GenericTableModel(String[] columnNames) {
            // Thêm cột checkbox và cột action vào mảng columnNames
            String[] enhancedColumns = new String[columnNames.length + 2];
            enhancedColumns[0] = ""; // Cột checkbox
            System.arraycopy(columnNames, 0, enhancedColumns, 1, columnNames.length);
            enhancedColumns[enhancedColumns.length - 1] = ""; // Cột action
            
            this.columnNames = enhancedColumns;
            
            // Create column key map for internationalization
            this.columnKeyMap = new HashMap<>();
            String tablePrefix = panelTitle.toLowerCase().replace(" ", "");
            for (int i = 1; i < enhancedColumns.length - 1; i++) {
                // Create a standard key format for columns: table.column.columnname
                String columnKey = "table." + tablePrefix + "." + 
                    enhancedColumns[i].toLowerCase().replace(" ", "").replace("↓", "");
                columnKeyMap.put(i, columnKey);
            }
        }
        
        /**
         * Return the internationalization key for a specific column
         * 
         * @param column The column index
         * @return The i18n key for the column, or null if not found
         */
        public String getColumnKey(int column) {
            return columnKeyMap.get(column);
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
            if (column == 0 || column == columnNames.length - 1) {
                // Return empty string for checkbox and action columns
                return columnNames[column];
            }
            
            // Always try to get localized column name if language manager exists
            if (languageManager != null) {
                String key = columnKeyMap.get(column);
                if (key != null) {
                    String localizedName = languageManager.getText(key);
                    // Only use the localized name if a real translation was found
                    if (localizedName != null && !localizedName.equals(key)) {
                        return localizedName;
                    }
                }
            }
            
            // Fallback to hardcoded column name
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
                return (new FlatSVGIcon("icons/ThreeDotsVertical.svg", 1.1f));
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
    private class CheckBoxHeaderRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxHeaderRenderer(JTable table, int columnIndex) {
            setHorizontalAlignment(CENTER);
            setBorderPainted(true);
            
            table.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int viewColumn = table.columnAtPoint(e.getPoint());
                    int modelColumn = table.convertColumnIndexToModel(viewColumn);
                    if (modelColumn == columnIndex) {
                        boolean newSelected = !isSelected();
                        setSelected(newSelected);
                        
                        GenericTableModel<?> model = (GenericTableModel<?>) table.getModel();
                        for (int i = 0; i < model.getRowCount(); i++) {
                            model.setValueAt(newSelected, i, columnIndex);
                        }
                    }
                }
            });
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            return this;
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
            setIcon(new FlatSVGIcon("icons/ThreeDotsVertical.svg", 1f));
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
            button = new JButton(new FlatSVGIcon("icons/ThreeDotsVertical.svg", 1.1f));
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
            return (new FlatSVGIcon("icons/ThreeDotsVertical.svg", 1.1f));
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
        
        JMenuItem editItem = new JMenuItem(languageManager != null ? 
            languageManager.getText("menu.edit") : "Edit");
        editItem.addActionListener(e -> editEntity(entity));
        
        JMenuItem activateItem = new JMenuItem(languageManager != null ? 
            languageManager.getText("menu.activate") : "Activate");
        activateItem.addActionListener(e -> activateEntity(entity));
        activateItem.setEnabled(!entity.isActive());
        
        JMenuItem deactivateItem = new JMenuItem(languageManager != null ? 
            languageManager.getText("menu.deactivate") : "Deactivate");
        deactivateItem.addActionListener(e -> deactivateEntity(entity));
        deactivateItem.setEnabled(entity.isActive());
        
        JMenuItem deleteItem = new JMenuItem(languageManager != null ? 
            languageManager.getText("menu.delete") : "Delete");
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
        if (editDialogClass != null) {
            try {
//                GenericEditDialog editDialog = editDialogClass.getDeclaredConstructor().newInstance();
                GenericEditDialog editDialog = applicationState.applicationContext.getBean(editDialogClass);
                this.editDialog = editDialog;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create dialog", e);
            }
            editDialog.showDialog(entity);
        } else {
            RouteParams params = new RouteParams();
            params.set("entity", entity);
            for (AppView view:AppView.values()){
                if(view.getPanelClass().equals(editPanelClass)){
                    this.navigationService.navigateTo(view, params);
                    return;
                }
            }
            this.navigationService.navigateTo(editPanelClass, params);
        }
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

    /**
     * Custom header renderer that ensures column names are always up-to-date with current language
     */
    private class LocalizedHeaderRenderer extends DefaultTableCellRenderer {
        public LocalizedHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setBackground(new Color(240, 240, 240));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(220, 220, 220)));
            setFont(AppFont.DEFAULT_FONT);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Get the model column index which may be different from the view column index
            int modelColumn = table.convertColumnIndexToModel(column);
            
            // Set the value with the latest translation if available
            if (modelColumn > 0 && modelColumn < tableModel.getColumnCount() - 1 && languageManager != null) {
                String key = tableModel.getColumnKey(modelColumn);
                if (key != null) {
                    String localizedName = languageManager.getText(key);
                    if (localizedName != null && !localizedName.equals(key)) {
                        setText(localizedName);
                        return this;
                    }
                }
            }
            
            // Default fallback
            setText(value != null ? value.toString() : "");
            return this;
        }
    }
}

package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.common.PageResponse;
import com.nhom4.nhtsstore.repositories.specification.SpecSearchCriteria;
import com.nhom4.nhtsstore.repositories.specification.SearchOperation;
import com.nhom4.nhtsstore.repositories.specification.UserSpecification;
import com.nhom4.nhtsstore.repositories.specification.UserSpecificationsBuilder;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.ui.shared.components.GlobalLoadingManager;
import com.nhom4.nhtsstore.utils.JavaFxThemeUtil;
import com.nhom4.nhtsstore.viewmodel.user.UserRecordVm;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Controller
public class UserListFxController implements Initializable {

    private final IUserService userService;
    private final ThemeManager themeManager;
    private final LanguageManager languageManager;
    private final ApplicationState applicationState;
    // UI components from FXML
    @FXML private BorderPane mainPane;
    @FXML private MFXTextField searchField;
    @FXML private MFXButton createButton;
    @FXML private MFXButton refreshButton;
    @FXML private MFXTableView<UserRecordVm> tableView;
    @FXML private MFXPagination pagination;
    @FXML private MFXComboBox<Integer> pageSizeCombo;
    @FXML private Label totalItemsLabel;
    @FXML private Label pageInfoLabel;
    @FXML private Label titleLabel;
    private Timeline searchDebounceTimer;
    // State variables
    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private long totalItems = 0;
    private final List<String> searchFields = List.of("username", "fullName", "email");
    // Callbacks
    private Consumer<UserRecordVm> onEditUser;
    private Runnable onCreateUser;
    private Consumer<UserRecordVm> onDeleteUser;

    public UserListFxController(
            IUserService userService,
            ThemeManager themeManager,
            LanguageManager languageManager, ApplicationState applicationState) {
        this.userService = userService;
        this.themeManager = themeManager;
        this.languageManager = languageManager;
        this.applicationState = applicationState;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up theme listener
        JavaFxThemeUtil.setupThemeListener(mainPane, themeManager);

        // Configure UI components
        configureComponents();

        // Set up table
        configureTableView();

        // Update UI texts with current language
        updateTexts();

        // Listen for language changes
        languageManager.addLanguageChangeListener(this::updateTexts);

//        onNavigate();
    }


    public void onNavigate() {
        if (applicationState.isAuthenticated()) {
            loadData();
        }
    }
    private void configureComponents() {
        // Configure search field
        searchField.setFloatMode(FloatMode.ABOVE);
        searchDebounceTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            currentPage = 0; // Reset to first page on search
            loadData();
        }));
        searchDebounceTimer.setCycleCount(1);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchDebounceTimer.stop();
            searchDebounceTimer.playFromStart();
        });
        
        // Configure page size combo
        pageSizeCombo.setItems(FXCollections.observableArrayList(10, 20, 50, 100));
        pageSizeCombo.setPopupAlignment(Alignment.of(HPos.CENTER, VPos.TOP)); // Set popup to appear above the combo box
        
        pageSizeCombo.selectItem(pageSize);
        pageSizeCombo.setOnAction(e -> {
            pageSize = pageSizeCombo.getSelectedItem();
            currentPage = 0; // Reset to first page when changing page size
            loadData();
        });
        
        // Configure pagination
        pagination.setMaxPage(5);
        pagination.setCurrentPage(1);
        pagination.currentPageProperty().addListener((obs, oldPage, newPage) -> {
            if (newPage.intValue() != currentPage + 1) {
                currentPage = newPage.intValue() - 1;
                loadData();
            }
        });
    }
    
    private void configureTableView() {
        tableView.setFooterVisible(false);

        // Configure columns
        MFXTableColumn<UserRecordVm> idColumn = new MFXTableColumn<>(languageManager.getText("table.users.id"), true);
        idColumn.setRowCellFactory(user -> new MFXTableRowCell<>(UserRecordVm::getUserId));
        idColumn.setPrefWidth(100);
        
        MFXTableColumn<UserRecordVm> usernameColumn = new MFXTableColumn<>(languageManager.getText("table.users.username"), true);
        usernameColumn.setRowCellFactory(user -> new MFXTableRowCell<>(UserRecordVm::getUsername));
        usernameColumn.setPrefWidth(250);

        MFXTableColumn<UserRecordVm> nameColumn = new MFXTableColumn<>(languageManager.getText("table.users.fullname"), true);
        nameColumn.setRowCellFactory(user -> new MFXTableRowCell<>(UserRecordVm::getFullName));
        nameColumn.setPrefWidth(300);

        MFXTableColumn<UserRecordVm> emailColumn = new MFXTableColumn<>(languageManager.getText("table.users.email"), true);
        emailColumn.setRowCellFactory(user -> new MFXTableRowCell<>(UserRecordVm::getEmail));
        emailColumn.setPrefWidth(200);

        MFXTableColumn<UserRecordVm> statusColumn = new MFXTableColumn<>(languageManager.getText("table.users.status"), true);
        statusColumn.setRowCellFactory(user -> {
            MFXTableRowCell<UserRecordVm, String> cell = new MFXTableRowCell<>(u -> u.isActive() ? 
                    languageManager.getText("status.active") : 
                    languageManager.getText("status.inactive"));
            
            // Apply custom styling based on status directly
            if (user.isActive()) {
                cell.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                cell.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
            
            return cell;
        });
        statusColumn.setPrefWidth(150);
        
        MFXTableColumn<UserRecordVm> actionsColumn = new MFXTableColumn<>("", false);
        actionsColumn.setRowCellFactory(user -> {
            // Create the dot menu button
            MFXButton menuButton = new MFXButton("⋮");
            menuButton.setStyle("-fx-font-size: 18px; -fx-background-color: transparent; -fx-padding: 5 10 5 10;");
            
            // Create a popup container for the menu items
            VBox popupContent = new VBox(5);
            popupContent.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 4; " +
                                 "-fx-background-radius: 4; -fx-padding: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
            
            // Create menu items as buttons
            MFXButton editButton = new MFXButton(languageManager.getText("menu.edit"));
            editButton.setPrefWidth(120);
            editButton.setOnAction(e -> {
                if (onEditUser != null) {
                    onEditUser.accept(user);
                }
            });
            
            MFXButton deleteButton = new MFXButton(languageManager.getText("menu.delete"));
            deleteButton.setPrefWidth(120);
            deleteButton.setOnAction(e -> {
                // Handle delete action
                if (onDeleteUser != null) {
                    onDeleteUser.accept(user);
                }
            });
            
            // Add buttons to popup
            popupContent.getChildren().addAll(editButton, deleteButton);
            
            // Create popup
            Popup popup = new Popup();
            popup.getContent().add(popupContent);
            popup.setAutoHide(true);
            
            // Show popup when menu button is clicked
            menuButton.setOnAction(e -> {
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    // Position the popup below the button
                    popup.show(menuButton, 
                        menuButton.localToScreen(menuButton.getBoundsInLocal()).getMinX()+20 , // offset to center the popup
                        menuButton.localToScreen(menuButton.getBoundsInLocal()).getMaxY()-50);
                }
            });
            
            // Create cell with the menu button
            MFXTableRowCell<UserRecordVm, String> cell = new MFXTableRowCell<>(u -> "");
            cell.setGraphic(menuButton);
            cell.setAlignment(Pos.CENTER);
            
            return cell;
        });
        actionsColumn.setPrefWidth(15);
        
        tableView.getTableColumns().addAll(idColumn, usernameColumn, nameColumn, emailColumn, statusColumn, actionsColumn);
        
        // Double-click to edit
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tableView.getSelectionModel().getSelectedValues().isEmpty()) {
                UserRecordVm selectedUser = tableView.getSelectionModel().getSelectedValues().get(0);
                if (selectedUser != null && onEditUser != null) {
                    onEditUser.accept(selectedUser);
                }
            }
        });
    }

    private void updateTexts() {
        Platform.runLater(() -> {
            try {
                // Update all FXML-defined labels
                if (titleLabel != null) {
                    titleLabel.setText(languageManager.getText("panel.user"));
                }

                if (searchField != null) {
                    searchField.setFloatingText(languageManager.getText("search.users"));
                }

                if (createButton != null) {
                    createButton.setText(languageManager.getText("button.new"));
                }

                if (refreshButton != null) {
                    refreshButton.setText(languageManager.getText("button.refresh"));
                }

                if (pageSizeCombo != null) {
                    pageSizeCombo.setFloatingText(languageManager.getText("pagination.pageSize"));
                }

                // Update table column headers
                updateTableColumnTexts();

                // Update pagination labels
                updateItemCountLabel();
                updatePageInfoLabel();
            } catch (Exception e) {
                System.err.println("Error updating texts: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void updateTableColumnTexts() {
        if (tableView != null && !tableView.getTableColumns().isEmpty()) {
            tableView.getTableColumns().get(0).setText(languageManager.getText("table.users.id"));
            tableView.getTableColumns().get(1).setText(languageManager.getText("table.users.username"));
            tableView.getTableColumns().get(2).setText(languageManager.getText("table.users.fullname"));
            tableView.getTableColumns().get(3).setText(languageManager.getText("table.users.email"));
            tableView.getTableColumns().get(4).setText(languageManager.getText("table.users.status"));
            tableView.getTableColumns().get(5).setText("");
        }
    }
    
    private void updateItemCountLabel() {
        if (totalItemsLabel != null) {
            totalItemsLabel.setText(String.format(
                languageManager.getText("pagination.totalRecords"),
                totalItems
            ));
        }
    }
    
    private void updatePageInfoLabel() {
        if (pageInfoLabel != null) {
            int start = currentPage * pageSize + 1;
            int end = Math.min((currentPage + 1) * pageSize, (int) totalItems);
            if (totalItems == 0) {
                start = 0;
                end = 0;
            }
            
            pageInfoLabel.setText(String.format(
                languageManager.getText("pagination.info"),
                currentPage + 1, totalPages, start, end, totalItems
            ));
        }
    }
    
    public void loadData() {
        // Show loading indicator
        GlobalLoadingManager.getInstance().showSpinner();
        
        // Use a background thread to load data
        Thread.startVirtualThread(() -> {
            try {
                PageResponse<UserRecordVm> response;
                String searchTerm = searchField != null ? searchField.getText() : "";
                
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    Pageable pageable = PageRequest.of(currentPage, pageSize);
                    response = userService.searchUsers(searchTerm,searchFields,pageable );
                } else {
                    // Get all users
                    response = userService.findAllUsers(currentPage, pageSize, "lastModifiedOn", "desc");
                }
                
                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    updateTableData(response);
                    GlobalLoadingManager.getInstance().hideSpinner();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    // Handle error
                    e.printStackTrace();
                    GlobalLoadingManager.getInstance().hideSpinner();
                });
            }
        });
    }
    
    private void updateTableData(PageResponse<UserRecordVm> response) {
        // Update state variables
        totalItems = response.getTotalElements();
        totalPages = response.getTotalPages();
        
        // Update table data
        ObservableList<UserRecordVm> data = FXCollections.observableArrayList(response.getContent());
        tableView.setItems(data);
        
        // Update pagination
        pagination.setMaxPage(totalPages);
        pagination.setCurrentPage(currentPage + 1);
        
        // Update labels
        updateItemCountLabel();
        updatePageInfoLabel();
    }
    
    // Event handlers
    @FXML
    private void onRefresh(ActionEvent event) {
        loadData();
    }
    
    @FXML
    private void onCreateUser(ActionEvent event) {
        if (onCreateUser != null) {
            onCreateUser.run();
        }
    }
    
    // Setters for callbacks
    public void setOnEditUser(Consumer<UserRecordVm> callback) {
        this.onEditUser = callback;
    }
    
    public void setOnCreateUser(Runnable callback) {
        this.onCreateUser = callback;
    }
    
    public void setOnDeleteUser(Consumer<UserRecordVm> callback) {
        this.onDeleteUser = callback;
    }
} 

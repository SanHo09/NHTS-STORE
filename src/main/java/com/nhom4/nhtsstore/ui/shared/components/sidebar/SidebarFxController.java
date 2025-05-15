package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import com.nhom4.nhtsstore.ui.AppView;
import com.nhom4.nhtsstore.ui.ApplicationState;
import com.nhom4.nhtsstore.ui.navigation.NavigationService;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import com.nhom4.nhtsstore.ui.shared.LanguageManager;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.UIUtils;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SidebarFxController implements Initializable, LanguageManager.LanguageChangeListener {

    private final ApplicationState applicationState;
    private final NavigationService navigationService;
    private final LanguageManager languageManager;
    private Map<AppView, Node> menuItemNodes = new HashMap<>();
    private Map<AppView, VBox> submenuContainers = new HashMap<>();
    private Map<AppView, ImageView> menuArrows = new HashMap<>();
    private AppView selectedView = null;

    // Add a lock to prevent concurrent refreshes
    private final Object refreshLock = new Object();
    private boolean isRefreshing = false;

    @FXML
    private VBox menuContainer;

    public SidebarFxController(ApplicationState applicationState, 
                              NavigationService navigationService,
                              LanguageManager languageManager) {
        this.applicationState = applicationState;
        this.navigationService = navigationService;
        this.languageManager = languageManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Register language change listener
        languageManager.addLanguageChangeListener(this);
        
        // Initial build of menu
        refreshMenuItems();
        
        // Listen for authentication changes
        applicationState.authenticatedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(this::refreshMenuItems);
            }
        });
    }
    
    @Override
    public void onLanguageChanged() {
        refreshMenuItems();
    }
    
    public void setSelectedView(AppView view) {
        // Ensure we're on the JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setSelectedView(view));
            return;
        }
        
        try {
            // If a view is already selected, remove selection styling
            if (selectedView != null && menuItemNodes.containsKey(selectedView)) {
                Node node = menuItemNodes.get(selectedView);
                if (node != null) {
                    node.getStyleClass().remove("selected");
                    node.getStyleClass().remove("solo-parent-menu-item");
                }
            }
            
            // Set new selected view and apply selection styling
            selectedView = view;
            if (selectedView != null && menuItemNodes.containsKey(selectedView)) {
                Node node = menuItemNodes.get(selectedView);
                if (node != null) {
                    node.getStyleClass().add("selected");
                    node.getStyleClass().add("solo-parent-menu-item");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in setSelectedView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshMenuItems() {
        // Use a lock to prevent concurrent refreshes which could cause index issues
        synchronized (refreshLock) {
            // If already refreshing, don't start another refresh
            if (isRefreshing) {
                return;
            }
            isRefreshing = true;
        }
        
        Platform.runLater(() -> {
            try {
                // Ensure we're on the JavaFX thread
                if (!Platform.isFxApplicationThread()) {
                    refreshMenuItems();
                    return;
                }
                
                menuContainer.getChildren().clear();
                menuItemNodes.clear();
                submenuContainers.clear();
                menuArrows.clear();
                
                // Group menu items by parent
                Map<AppView, List<AppView>> menuGroups = new LinkedHashMap<>();
                
                // Add null group for top-level items
                menuGroups.put(null, new ArrayList<>());
                
                try {
                    // Go through all AppView values and categorize them
                    for (AppView view : AppView.values()) {
                        if (view == AppView.LOGIN) {
                            continue; // Skip LOGIN
                        }
                        
                        // Skip menu items the user doesn't have permission for
                        if (!hasMenuPermission(view)) {
                            continue;
                        }
                        
                        AppView parent = view.getParent();
                        
                        if (parent == null) {
                            // This is a top-level menu item
                            menuGroups.get(null).add(view);
                        } else {
                            // This is a submenu item
                            if (!menuGroups.containsKey(parent)) {
                                menuGroups.put(parent, new ArrayList<>());
                            }
                            menuGroups.get(parent).add(view);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error categorizing menu items: " + e.getMessage());
                    e.printStackTrace();
                }
                
                try {
                    // Process top-level items first
                    for (AppView parent : menuGroups.get(null)) {
                        // Check if this has submenus
                        boolean hasSubmenus = menuGroups.containsKey(parent) && !menuGroups.get(parent).isEmpty();
                        
                        if (hasSubmenus) {
                            // Create category section
                            VBox categorySection = new VBox();
                            categorySection.setSpacing(5);
                            
                            // 1. Create category header (parent menu item that's clickable)
                            Label parentMenuItem = createParentMenuItem(parent, menuGroups.get(parent));
                            parentMenuItem.getStyleClass().add("parent-menu-item");
                            categorySection.getChildren().add(parentMenuItem);
                            
                            // 2. Create submenu container (will be shown/hidden when clicking the arrow)
                            VBox subMenuContainer = new VBox();
                            subMenuContainer.setSpacing(2);
                            subMenuContainer.setPadding(new Insets(0, 0, 0, 15)); // Left padding for indentation
                            subMenuContainer.setVisible(false); // Initially collapsed
                            subMenuContainer.setManaged(false); // Don't take up space when collapsed
                            submenuContainers.put(parent, subMenuContainer);
                            
                            // Add submenu items
                            List<AppView> children = menuGroups.get(parent);
                            if (children != null) {
                                for (AppView child : children) {
                                    Label subMenuItem = createSubMenuItem(child);
                                    subMenuContainer.getChildren().add(subMenuItem);
                                }
                            }
                            
                            categorySection.getChildren().add(subMenuContainer);
                            menuContainer.getChildren().add(categorySection);
                        } else {
                            // Create normal menu item for parent without submenus
                            Label menuItem = createMenuItem(parent);
                            menuItem.getStyleClass().add("parent-menu-item"); // Use parent-menu-item style
                            menuContainer.getChildren().add(menuItem);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error building menu UI: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // Re-select current view if applicable
                if (selectedView != null) {
                    try {
                        setSelectedView(selectedView);
                    } catch (Exception e) {
                        System.err.println("Error setting selected view: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Unhandled error in refreshMenuItems: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Always release the lock when done
                synchronized (refreshLock) {
                    isRefreshing = false;
                }
            }
        });
    }
    
    private Label createMenuItem(AppView view) {
        HBox menuItemContent = new HBox();
        menuItemContent.setSpacing(10);
        menuItemContent.setMinHeight(40);
        menuItemContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Add icon if available - use IconUtil to load SVG icons
        if (view.getIcon() != null && !view.getIcon().isEmpty()) {
            // Use IconUtil to load SVG icon with white color
            ImageView icon = null;
            try {
                icon = IconUtil.createFxImageViewFromSvg(
                    "/icons/" + view.getIcon(), 
                    20,  // width 
                    20,  // height
                    // Use white color for all menu icons
                    color -> Color.WHITE
                );
                
                if (icon != null) {
                    menuItemContent.getChildren().add(icon);
                }
            } catch (Exception e) {
                // Fallback to old method if SVG loading fails
                try {
                    ImageView fallbackIcon = new ImageView();
                    fallbackIcon.setFitWidth(20);
                    fallbackIcon.setFitHeight(20);
                    fallbackIcon.setImage(UIUtils.loadJavaFXImage(view.getIcon()));
                    menuItemContent.getChildren().add(fallbackIcon);
                } catch (Exception ex) {
                    // If all icon loading fails, skip it
                }
            }
        }
        
        // Add label
        Label textLabel = new Label(getLocalizedMenuName(view));
        textLabel.setStyle("-fx-text-fill: white;");
        menuItemContent.getChildren().add(textLabel);
        
        Label menuItem = new Label();
        menuItem.setGraphic(menuItemContent);
        menuItem.getStyleClass().add("menu-item");
        menuItem.setMaxWidth(Double.MAX_VALUE);
        
        // Store in map for selection tracking
        menuItemNodes.put(view, menuItem);
        
        // Add click event
        menuItem.setOnMouseClicked(event -> {
            try {
                navigationService.navigateTo(view, new RouteParams());
                setSelectedView(view);
            } catch (Exception e) {
                System.err.println("Error handling menu item click: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return menuItem;
    }
    
    private Label createParentMenuItem(AppView view, List<AppView> children) {
        HBox menuItemContent = new HBox();
        menuItemContent.setSpacing(10);
        menuItemContent.setMinHeight(40);
        menuItemContent.setAlignment(Pos.CENTER_LEFT);
        
        // Add icon if available - use IconUtil to load SVG icons
        if (view.getIcon() != null && !view.getIcon().isEmpty()) {
            // Use IconUtil to load SVG icon with white color
            ImageView icon = null;
            try {
                icon = IconUtil.createFxImageViewFromSvg(
                    "/icons/" + view.getIcon(), 
                    20,  // width 
                    20,  // height
                    // Use white color for all menu icons
                    color -> Color.WHITE
                );
                
                if (icon != null) {
                    menuItemContent.getChildren().add(icon);
                }
            } catch (Exception e) {
                // Fallback to old method if SVG loading fails
                try {
                    ImageView fallbackIcon = new ImageView();
                    fallbackIcon.setFitWidth(20);
                    fallbackIcon.setFitHeight(20);
                    fallbackIcon.setImage(UIUtils.loadJavaFXImage(view.getIcon()));
                    menuItemContent.getChildren().add(fallbackIcon);
                } catch (Exception ex) {
                    // If all icon loading fails, skip it
                }
            }
        }
        
        // Add label - this is clickable for navigation
        Label textLabel = new Label(getLocalizedMenuName(view));
        textLabel.setStyle("-fx-text-fill: white;");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textLabel, Priority.ALWAYS);
        
        menuItemContent.getChildren().add(textLabel);
        
        // Add spacer to push the arrow to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        menuItemContent.getChildren().add(spacer);
        
        // Create a button with a large arrow character
        Button arrowButton = new Button("▼");
        arrowButton.getStyleClass().add("menu-arrow-button");
        arrowButton.setFocusTraversable(false);
        
        // Store data about expanded state in user data
        arrowButton.setUserData(false); // false = collapsed, true = expanded
        
        // Only the arrow button toggles the submenu visibility
        arrowButton.setOnAction(event -> {
            try {
                VBox subMenuContainer = submenuContainers.get(view);
                if (subMenuContainer != null) {
                    // Get current state (safely)
                    boolean isVisible = subMenuContainer.isVisible();
                    Boolean isExpanded = (Boolean) arrowButton.getUserData();
                    if (isExpanded == null) {
                        isExpanded = false;
                        arrowButton.setUserData(false);
                    }
                    
                    // Update visibility
                    subMenuContainer.setVisible(!isVisible);
                    subMenuContainer.setManaged(!isVisible); // Also toggle managed property
                    
                    // Update the arrow character based on state
                    arrowButton.setText(isExpanded ? "▼" : "▶");
                    arrowButton.setUserData(!isExpanded);
                }
            } catch (Exception e) {
                System.err.println("Error toggling submenu: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Stop event propagation
            event.consume();
        });
        
        menuItemContent.getChildren().add(arrowButton);
        
        Label menuItem = new Label();
        menuItem.setGraphic(menuItemContent);
        menuItem.getStyleClass().add("menu-item");
        menuItem.setMaxWidth(Double.MAX_VALUE);
        
        // Make the entire parent menu item navigable (except the arrow button)
        menuItem.setOnMouseClicked(event -> {
            try {
                // Only navigate if the arrow button wasn't clicked
                if (!isChildOf(event.getTarget(), arrowButton)) {
                    navigationService.navigateTo(view, new RouteParams());
                    setSelectedView(view);
                }
            } catch (Exception e) {
                System.err.println("Error handling menu item click: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Store in map for selection tracking
        menuItemNodes.put(view, menuItem);
        
        return menuItem;
    }
    
    /**
     * Helper method to check if a node is a child of another node
     */
    private boolean isChildOf(Object target, Node parent) {
        if (target == null || parent == null) {
            return false;
        }
        
        if (target == parent) {
            return true;
        }
        
        if (target instanceof Node) {
            try {
                Node node = (Node) target;
                while (node != null) {
                    if (node == parent) {
                        return true;
                    }
                    node = node.getParent();
                }
            } catch (Exception e) {
                // If any error occurs during traversal, assume it's not a child
                return false;
            }
        }
        
        return false;
    }
    
    private Label createSubMenuItem(AppView view) {
        Label subMenuItem = new Label(getLocalizedMenuName(view));
        subMenuItem.getStyleClass().add("submenu-item");
        subMenuItem.setMaxWidth(Double.MAX_VALUE);
        
        // Store in map for selection tracking
        menuItemNodes.put(view, subMenuItem);
        
        // Add click event
        subMenuItem.setOnMouseClicked(event -> {
            try {
                navigationService.navigateTo(view, new RouteParams());
                setSelectedView(view);
            } catch (Exception e) {
                System.err.println("Error handling submenu item click: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        return subMenuItem;
    }
    
    private String getLocalizedMenuName(AppView view) {
        String key = "nav." + view.name().toLowerCase();
        return languageManager.getText(key);
    }
    
    private boolean hasMenuPermission(AppView view) {
        // If user isn't logged in, only show LOGIN
        if (!applicationState.isAuthenticated()) {
            return view == AppView.LOGIN;
        }

        UserSessionVm currentUser = applicationState.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Check if user's role has access to this view
        String userRole = currentUser.getRole();
        if (userRole == null) {
            return false;
        }

        return view.isAccessibleByRole(userRole);
    }
} 
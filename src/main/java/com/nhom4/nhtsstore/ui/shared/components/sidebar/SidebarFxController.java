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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.Color;
import java.net.URL;
import java.util.*;

@Component
public class SidebarFxController implements Initializable, LanguageManager.LanguageChangeListener {
    private final ApplicationState applicationState;
    private final NavigationService navigationService;
    private final LanguageManager languageManager;
    private Map<AppView, Node> menuItemNodes = new HashMap<>();
    private Map<AppView, VBox> submenuContainers = new HashMap<>();
    private Map<AppView, ImageView> menuArrows = new HashMap<>();
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
        languageManager.addLanguageChangeListener(this);
        refreshMenuItems();

        applicationState.authenticatedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(this::refreshMenuItems);
            }else {
                Platform.runLater(() -> {
                    menuContainer.getChildren().clear();
                    menuItemNodes.clear();
                    submenuContainers.clear();
                    menuArrows.clear();
                });
            }
        });

        applicationState.currentViewProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) Platform.runLater(() -> setSelectedView(newValue));
        });
        navigationService.addNavigationListener(view -> {
            SwingUtilities.invokeLater(() -> setSelectedView(view));
        });
    }

    @Override
    public void onLanguageChanged() {
        refreshMenuItems();
    }

    public void setSelectedView(AppView view) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setSelectedView(view));
            return;
        }

        try {
            menuItemNodes.values().stream()
                    .filter(Objects::nonNull)
                    .forEach(node -> {
                        node.getStyleClass().remove("selected");
                        node.getStyleClass().remove("solo-parent-menu-item");
                    });

            applicationState.getCurrentView().set(view);

            if (view != null && menuItemNodes.containsKey(view)) {
                Node node = menuItemNodes.get(view);
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
        synchronized (refreshLock) {
            if (isRefreshing) return;
            isRefreshing = true;
        }

        Platform.runLater(() -> {
            try {
                if (!Platform.isFxApplicationThread()) {
                    refreshMenuItems();
                    return;
                }

                menuContainer.getChildren().clear();
                menuItemNodes.clear();
                submenuContainers.clear();
                menuArrows.clear();

                Map<AppView, List<AppView>> menuGroups = groupMenuItems();
                buildMenuUI(menuGroups);

            } catch (Exception e) {
                System.err.println("Error in refreshMenuItems: " + e.getMessage());
                e.printStackTrace();
            } finally {
                synchronized (refreshLock) {
                    isRefreshing = false;
                }
            }
        });
    }

    private Map<AppView, List<AppView>> groupMenuItems() {
        Map<AppView, List<AppView>> menuGroups = new LinkedHashMap<>();
        menuGroups.put(null, new ArrayList<>());

        for (AppView view : AppView.values()) {
            if (!hasMenuPermission(view)) continue;

            AppView parent = view.getParent();
            if (parent == null) {
                menuGroups.get(null).add(view);
            } else {
                if (!menuGroups.containsKey(parent)) {
                    menuGroups.put(parent, new ArrayList<>());
                }
                menuGroups.get(parent).add(view);
            }
        }

        return menuGroups;
    }

    private void buildMenuUI(Map<AppView, List<AppView>> menuGroups) {
        for (AppView parent : menuGroups.get(null)) {
            boolean hasSubmenus = menuGroups.containsKey(parent) && !menuGroups.get(parent).isEmpty();

            if (hasSubmenus) {
                VBox categorySection = new VBox();
                categorySection.setSpacing(5);

                Label parentMenuItem = createParentMenuItem(parent, menuGroups.get(parent));
                parentMenuItem.getStyleClass().add("parent-menu-item");
                categorySection.getChildren().add(parentMenuItem);

                VBox subMenuContainer = new VBox();
                subMenuContainer.setSpacing(2);
                subMenuContainer.setPadding(new Insets(0, 0, 0, 15));
                subMenuContainer.setVisible(false);
                subMenuContainer.setManaged(false);
                submenuContainers.put(parent, subMenuContainer);

                List<AppView> children = menuGroups.get(parent);
                if (children != null) {
                    for (AppView child : children) {
                        subMenuContainer.getChildren().add(createSubMenuItem(child));
                    }
                }

                categorySection.getChildren().add(subMenuContainer);
                menuContainer.getChildren().add(categorySection);
            } else {
                Label menuItem = createMenuItem(parent);
                menuItem.getStyleClass().add("parent-menu-item");
                menuContainer.getChildren().add(menuItem);
            }
        }
    }

    private Label createMenuItem(AppView view) {
        HBox menuItemContent = new HBox();
        menuItemContent.setSpacing(10);
        menuItemContent.setMinHeight(40);
        menuItemContent.setAlignment(Pos.CENTER_LEFT);

        addIconToContainer(view, menuItemContent);

        Label textLabel = new Label(getLocalizedMenuName(view));
        textLabel.setStyle("-fx-text-fill: white;");
        menuItemContent.getChildren().add(textLabel);

        Label menuItem = new Label();
        menuItem.setGraphic(menuItemContent);
        menuItem.getStyleClass().add("menu-item");
        menuItem.setMaxWidth(Double.MAX_VALUE);
        menuItemNodes.put(view, menuItem);

        menuItem.setOnMouseClicked(event -> {
            try {
                navigationService.navigateTo(view, new RouteParams());
            } catch (Exception e) {
                System.err.println("Error handling menu item click: " + e.getMessage());
            }
        });

        return menuItem;
    }

    private Label createParentMenuItem(AppView view, List<AppView> children) {
        HBox menuItemContent = new HBox();
        menuItemContent.setSpacing(10);
        menuItemContent.setMinHeight(40);
        menuItemContent.setAlignment(Pos.CENTER_LEFT);

        addIconToContainer(view, menuItemContent);

        Label textLabel = new Label(getLocalizedMenuName(view));
        textLabel.setStyle("-fx-text-fill: white;");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textLabel, Priority.ALWAYS);
        menuItemContent.getChildren().add(textLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        menuItemContent.getChildren().add(spacer);

        Button arrowButton = new Button("▼");
        arrowButton.getStyleClass().add("menu-arrow-button");
        arrowButton.setFocusTraversable(false);
        arrowButton.setUserData(false);

        arrowButton.setOnAction(event -> {
            try {
                VBox subMenuContainer = submenuContainers.get(view);
                if (subMenuContainer != null) {
                    boolean isVisible = subMenuContainer.isVisible();
                    Boolean isExpanded = (Boolean) arrowButton.getUserData();
                    if (isExpanded == null) {
                        isExpanded = false;
                        arrowButton.setUserData(false);
                    }

                    subMenuContainer.setVisible(!isVisible);
                    subMenuContainer.setManaged(!isVisible);
                    arrowButton.setText(isExpanded ? "▼" : "▶");
                    arrowButton.setUserData(!isExpanded);
                }
            } catch (Exception e) {
                System.err.println("Error toggling submenu: " + e.getMessage());
            }
            event.consume();
        });

        menuItemContent.getChildren().add(arrowButton);

        Label menuItem = new Label();
        menuItem.setGraphic(menuItemContent);
        menuItem.getStyleClass().add("menu-item");
        menuItem.setMaxWidth(Double.MAX_VALUE);

        menuItem.setOnMouseClicked(event -> {
            try {
                if (!isChildOf(event.getTarget(), arrowButton)) {
                    navigationService.navigateTo(view, new RouteParams());
                    setSelectedView(view);
                }
            } catch (Exception e) {
                System.err.println("Error handling menu item click: " + e.getMessage());
            }
        });

        menuItemNodes.put(view, menuItem);
        return menuItem;
    }

    private void addIconToContainer(AppView view, HBox container) {
        if (view.getIcon() != null && !view.getIcon().isEmpty()) {
            try {
                ImageView icon = IconUtil.createFxImageViewFromSvg(
                        "/icons/" + view.getIcon(),
                        20, 20,
                        color -> Color.WHITE
                );

                if (icon != null) {
                    container.getChildren().add(icon);
                    return;
                }
            } catch (Exception e) {
                try {
                    ImageView fallbackIcon = new ImageView();
                    fallbackIcon.setFitWidth(20);
                    fallbackIcon.setFitHeight(20);
                    fallbackIcon.setImage(UIUtils.loadJavaFXImage(view.getIcon()));
                    container.getChildren().add(fallbackIcon);
                } catch (Exception ex) {
                    // Skip if icon loading fails
                }
            }
        }
    }

    private boolean isChildOf(Object target, Node parent) {
        if (target == null || parent == null) return false;
        if (target == parent) return true;

        if (target instanceof Node) {
            try {
                Node node = (Node) target;
                while (node != null) {
                    if (node == parent) return true;
                    node = node.getParent();
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private Label createSubMenuItem(AppView view) {
        Label subMenuItem = new Label(getLocalizedMenuName(view));
        subMenuItem.getStyleClass().add("submenu-item");
        subMenuItem.setMaxWidth(Double.MAX_VALUE);
        menuItemNodes.put(view, subMenuItem);

        subMenuItem.setOnMouseClicked(event -> {
            try {
                navigationService.navigateTo(view, new RouteParams());
                setSelectedView(view);
            } catch (Exception e) {
                System.err.println("Error handling submenu item click: " + e.getMessage());
            }
        });

        return subMenuItem;
    }

    private String getLocalizedMenuName(AppView view) {
        return languageManager.getText("nav." + view.name().toLowerCase());
    }

    private boolean hasMenuPermission(AppView view) {
        UserSessionVm currentUser = applicationState.getCurrentUser();
        if (currentUser == null) return false;

        String userRole = currentUser.getRole();
        if (userRole == null) return false;

        return view.isAccessibleByRole(userRole);
    }
}
package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.nhom4.nhtsstore.utils.IconUtil;
import com.nhom4.nhtsstore.utils.JavaFxSwing;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.*;

public class Model_Menu {

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public boolean isSubmenu() {
        return isSubmenu;
    }

    public void setSubmenu(boolean submenu) {
        isSubmenu = submenu;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean hasSubmenus() {
        return hasSubmenus;
    }

    public void setHasSubmenus(boolean hasSubmenus) {
        this.hasSubmenus = hasSubmenus;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public Model_Menu(String icon, String name, MenuType type) {
        this.icon = icon;
        this.name = name;
        this.type = type;
        this.isSubmenu = false;
        this.expanded = true;
        this.hasSubmenus = false;
        this.menuId = "";
    }

    public Model_Menu(String icon, String name, MenuType type, boolean isSubmenu) {
        this.icon = icon;
        this.name = name;
        this.type = type;
        this.isSubmenu = isSubmenu;
        this.expanded = true;
        this.hasSubmenus = false;
        this.menuId = "";
    }

    public Model_Menu(String icon, String name, MenuType type, String menuId, boolean hasSubmenus) {
        this.icon = icon;
        this.name = name;
        this.type = type;
        this.isSubmenu = false;
        this.expanded = true;
        this.hasSubmenus = hasSubmenus;
        this.menuId = menuId;
    }

    public Model_Menu() {
    }
    
    private String icon;
    private String name;
    private MenuType type;
    private boolean isSubmenu;
    private boolean expanded;
    private boolean hasSubmenus;
    private String menuId;
    
    public static enum MenuType {
        TITLE, MENU, SUBMENU, EMPTY
    }
}

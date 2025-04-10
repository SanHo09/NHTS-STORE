package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import javax.swing.Icon;
import javax.swing.ImageIcon;

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

    public Model_Menu(String icon, String name, MenuType type) {
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public Model_Menu() {
    }
    
    private String icon;
    private String name;
    private MenuType type;
    
    public Icon toIcon() {
        String path = "/icons/" + icon + ".png";
        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            return new ImageIcon();
        }
        return new ImageIcon(location);
    }
    
    public static enum MenuType {
        TITLE, MENU, EMPTY
    }
}

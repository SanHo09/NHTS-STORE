package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import com.nhom4.nhtsstore.ui.AppView;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class SidebarManager {
    @Setter
    private ListMenu listMenu;
    private final Map<AppView, Integer> menuIndexMap = new HashMap<>();

    public void registerMenuItem(AppView view, int index) {
        menuIndexMap.put(view, index);
    }

    public void selectMenuItem(AppView view) {
        if (listMenu != null && menuIndexMap.containsKey(view)) {
            selectMenuIndex(menuIndexMap.get(view));
        }
    }

    public void selectMenuIndex(int index) {
        if (listMenu != null) {
            listMenu.setSelectedIndex(index);
        }
    }

    // Map all AppView items to their menu positions
    public void initializeMenuMap() {
        int index = 0;
        for (AppView view : AppView.values()) {
            if (view == AppView.LOGIN) {
                continue;
            }
            menuIndexMap.put(view, index++);
        }
    }
    public void clearMenuMap() {
        menuIndexMap.clear();
    }
}
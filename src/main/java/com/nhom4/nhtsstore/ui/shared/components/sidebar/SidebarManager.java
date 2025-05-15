package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import com.nhom4.nhtsstore.ui.AppView;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Component
public class SidebarManager {
    @Setter
    private ListMenu listMenu;
    private final Map<AppView, Integer> menuIndexMap = new HashMap<>();
    private final List<AppView> menuItemsList = new ArrayList<>();

    public void registerMenuItem(AppView view, int index) {
        menuIndexMap.put(view, index);
        // Ensure list has enough capacity
        while (menuItemsList.size() <= index) {
            menuItemsList.add(null);
        }
        menuItemsList.set(index, view);
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

    /**
     * Get the AppView associated with a menu index
     * @param index The index in the visible menu
     * @return Optional containing the AppView if found
     */
    public Optional<AppView> getViewByIndex(int index) {
        if (index >= 0 && index < menuItemsList.size()) {
            return Optional.ofNullable(menuItemsList.get(index));
        }
        return Optional.empty();
    }

    // Map all AppView items to their menu positions
    public void initializeMenuMap() {
        menuIndexMap.clear();
        menuItemsList.clear();
        
        int index = 0;
        for (AppView view : AppView.values()) {
            if (view == AppView.LOGIN) {
                continue;
            }
            menuIndexMap.put(view, index);
            menuItemsList.add(view);
            index++;
        }
    }
    
    public void clearMenuMap() {
        menuIndexMap.clear();
        menuItemsList.clear();
    }
}
package com.nhom4.nhtsstore.ui.shared.components.sidebar;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListMenu<E extends Object> extends JList<E> implements MenuItem.MenuItemListener {
    private final DefaultListModel model;
    private int selectedIndex = -1;
    private int overIndex = -1;
    private EventMenuSelected event;
    private boolean isUpdating = false;
    private final Map<String, Boolean> expandStateMap = new HashMap<>();
    private final Map<String, List<Integer>> submenuIndexMap = new HashMap<>();
    private Consumer<String> accordionListener;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
    }

    public void setAccordionListener(Consumer<String> listener) {
        this.accordionListener = listener;
    }

    public ListMenu() {
        model = new DefaultListModel();
        setModel(model);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    int index = locationToIndex(me.getPoint());
                    Object o = model.getElementAt(index);
                    if (o instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) o;
                        if (menu.getType() == Model_Menu.MenuType.MENU) {
                            // Only change selection if clicking on non-parent menu or the parent menu text area
                            if (!menu.hasSubmenus() || me.getX() < getWidth() - 40) {
                                selectedIndex = index;
                                if (event != null) {
                                    event.selected(index);
                                }
                            }
                        }
                    } else {
                        selectedIndex = index;
                    }
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                overIndex = -1;
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                int index = locationToIndex(me.getPoint());
                if (index != overIndex) {
                    Object o = model.getElementAt(index);
                    if (o instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) o;
                        if (menu.getType() == Model_Menu.MenuType.MENU) {
                            overIndex = index;
                        } else {
                            overIndex = -1;
                        }
                        repaint();
                    }
                }
            }
        });
    }

    @Override
    public ListCellRenderer<? super E> getCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object o, int index, boolean selected, boolean focus) {
                Model_Menu data;
                if (o instanceof Model_Menu) {
                    data = (Model_Menu) o;
                } else {
                    data = new Model_Menu("", o + "", Model_Menu.MenuType.EMPTY);
                }
                MenuItem item = new MenuItem(data);
                item.setSelected(selectedIndex == index);
                item.setOver(overIndex == index);
                item.setMenuItemListener(ListMenu.this);

                // Set expanded state from our map
                if (data.hasSubmenus() && expandStateMap.containsKey(data.getMenuId())) {
                    item.setExpanded(expandStateMap.get(data.getMenuId()));
                }

                return item;
            }
        };
    }

    public void insertItemAt(Model_Menu data, int index) {
        model.add(index, data);
    }

    public DefaultListModel getModel() {
        return model;
    }

    public void addItem(Model_Menu data) {
        model.addElement(data);
    }

    @Override
    public void onAccordionToggled(String menuId, boolean expanded) {
        log.debug("ListMenu received toggle for: {} expanded: {}", menuId, expanded);
        expandStateMap.put(menuId, expanded);
        toggleSubmenuVisibility(menuId, expanded);
    }

    private void toggleSubmenuVisibility(String menuId, boolean expanded) {
        log.debug("Toggle submenu visibility for: {}, expanded: {}", menuId, expanded);
        log.debug("Has submenu mapping: {}", submenuIndexMap.containsKey(menuId));

        if (submenuIndexMap.containsKey(menuId)) {
            List<Integer> submenuIndices = submenuIndexMap.get(menuId);
            log.debug("Submenu indices: {}", submenuIndices);

            if (!expanded) {
                // Hide submenus - we need to build a new model without the submenu items
                DefaultListModel newModel = new DefaultListModel();
                int currentIndex = 0;

                // Copy all items except the submenus of this parent
                for (int i = 0; i < model.size(); i++) {
                    if (!submenuIndices.contains(i)) {
                        newModel.addElement(model.getElementAt(i));
                        currentIndex++;
                    } else {
                        log.debug("Hiding submenu at index: {}", i);
                    }
                }

                // Replace the model
                model.clear();
                for (int i = 0; i < newModel.size(); i++) {
                    model.addElement(newModel.getElementAt(i));
                }
            } else {
                // Show submenus - need to rebuild the entire menu
                // Find the expanded menu's parent index in the current model
                int parentIndex = -1;
                for (int i = 0; i < model.size(); i++) {
                    Object item = model.getElementAt(i);
                    if (item instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) item;
                        if (menu.getMenuId().equals(menuId)) {
                            parentIndex = i;
                            break;
                        }
                    }
                }

                if (parentIndex >= 0) {
                    log.debug("Found parent at index: {}", parentIndex);
                    // We need to re-insert all submenu items after the parent
                    if (accordionListener != null) {
                        accordionListener.accept(menuId);
                    }
                }
            }

            // Update submenu indices after toggling
            updateSubmenuIndices();

            repaint();
        }
    }

    public void registerParentSubmenuRelationship(String parentId, int parentIndex, List<Integer> submenuIndices) {
        submenuIndexMap.put(parentId, new ArrayList<>(submenuIndices));
        expandStateMap.put(parentId, true); // Default to expanded
    }

    private void updateSubmenuIndices() {
        // Rebuild submenuIndexMap based on current model state
        Map<String, List<Integer>> newMap = new HashMap<>();

        for (String parentId : submenuIndexMap.keySet()) {
            List<Integer> newIndices = new ArrayList<>();

            // Find the parent index
            int parentIndex = -1;
            for (int i = 0; i < model.size(); i++) {
                Object item = model.getElementAt(i);
                if (item instanceof Model_Menu) {
                    Model_Menu menu = (Model_Menu) item;
                    if (menu.getMenuId().equals(parentId)) {
                        parentIndex = i;
                        break;
                    }
                }
            }

            // If parent is found, find its submenus
            if (parentIndex >= 0) {
                for (int i = parentIndex + 1; i < model.size(); i++) {
                    Object item = model.getElementAt(i);
                    if (item instanceof Model_Menu) {
                        Model_Menu menu = (Model_Menu) item;
                        if (menu.isSubmenu()) {
                            newIndices.add(i);
                        } else {
                            // Stop when we hit another parent menu
                            break;
                        }
                    }
                }

                newMap.put(parentId, newIndices);
            }
        }

        submenuIndexMap.clear();
        submenuIndexMap.putAll(newMap);
    }

    public void setSelectedIndex(int index) {
        // Check if we're already in the process of updating to prevent recursion
        if (isUpdating) {
            return;
        }

        if (index >= 0 && index < model.size()) {
            Object o = model.getElementAt(index);
            if (o instanceof Model_Menu) {
                Model_Menu menu = (Model_Menu) o;
                if (menu.getType() == Model_Menu.MenuType.MENU) {
                    selectedIndex = index;

                    // Set flag to prevent recursive calls
                    isUpdating = true;
                    try {
                        if (event != null) {
                            event.selected(index);
                        }
                    } finally {
                        // Always reset the flag, even if an exception occurs
                        isUpdating = false;
                    }

                    repaint();
                }
            }
        }
    }

    public void clearMenuItems() {
        model.clear();
        selectedIndex = -1;
        overIndex = -1;
        expandStateMap.clear();
        submenuIndexMap.clear();
        repaint();
    }
}

package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.RoleService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
public class RoleListPanel extends GenericTablePanel<Role> {
    private static final String[] ROLE_COLUMNS = {
            "Role Name", "Description", "Status"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("roleName");

    public RoleListPanel(RoleService roleService) {
        super(roleService, Role.class, RoleEditPanel.class, ROLE_COLUMNS, "Role Management", SEARCH_FIELDS);

        EventBus.getReloadSubject().subscribe(isReload -> {
            if ((Boolean) isReload) {
                this.loadData();
                EventBus.postReload(false);
            }
        });
    }
}
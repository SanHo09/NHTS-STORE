package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.RoleService;
import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
@Scope("prototype")

@Controller
public class RoleListPanel extends GenericTablePanel<Role> implements RoutablePanel {
    private final UserService userService;
    private static final String[] ROLE_COLUMNS = {
            "Role Name", "Description", "Status"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("roleName");

    public RoleListPanel(RoleService roleService, UserService userService) {
        super(roleService, Role.class, RoleEditPanel.class, ROLE_COLUMNS, "Role Management", SEARCH_FIELDS);
        this.userService = userService;

        EventBus.getReloadSubject().subscribe(isReload -> {
            if ((Boolean) isReload) {
                this.loadData();
                EventBus.postReload(false);
            }
        });
    }


    @Override
    public void onNavigate(RouteParams params) {
        if(!userService.isSuperAdmin()) {
            throw new SecurityException("You do not have permission to access this page");
        }
    }
}
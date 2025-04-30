package com.nhom4.nhtsstore.ui.page.permission;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.services.GenericService;
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
public class PermissionListPanel extends GenericTablePanel<Permission> implements RoutablePanel {
    private final UserService userService;
    private static final String[] columnNames = {
            "Name", "Description", "Status"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("Name");
    public PermissionListPanel(GenericService<Permission> service, UserService userService) {
        super(service, Permission.class, PermissionEditPanel.class, columnNames, "Permission Management", SEARCH_FIELDS);
        this.userService = userService;
    }

    @Override
    public void onNavigate(RouteParams params) {
        if(!userService.isSuperAdmin()) {
            throw new SecurityException("You do not have permission to access this page");
        }
    }
}

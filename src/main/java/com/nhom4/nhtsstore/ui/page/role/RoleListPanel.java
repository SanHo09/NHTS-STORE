package com.nhom4.nhtsstore.ui.page.role;

import com.nhom4.nhtsstore.entities.rbac.Role;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.services.IRoleService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;
@Scope("prototype")

@Controller
public class RoleListPanel extends GenericTablePanel<Role> {
    private static final String[] ROLE_COLUMNS = {
            "Role Name", "Description", "Status", "Updated At ↓", "Updated By"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("roleName");
    private static String placeHolderMessage = "Search by Role name";

    public RoleListPanel(IRoleService roleService) {
        super(roleService, Role.class, null,null, RoleEditDialog.class, ROLE_COLUMNS, "Roles", SEARCH_FIELDS, placeHolderMessage);
        
        // Cấu hình độ rộng cột
        int[] columnWidths = {
            40,    // checkbox
            150,   // Role Name
            150,   // Description
            80,   // Status
            150,   // Updated At
            150    // Updated By
        };
        
        configureColumnWidths(columnWidths);
        
        setHeaderAlignment(SwingConstants.LEFT);
        
        EventBus.getReloadSubject().subscribe(isReload -> {
            if ((Boolean) isReload) {
                this.loadData();
                EventBus.postReload(false);
            }
        });
    }
}
package com.nhom4.nhtsstore.ui.page.permission;

import com.nhom4.nhtsstore.entities.rbac.Permission;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;

@Scope("prototype")
@Controller
public class PermissionListPanel extends GenericTablePanel<Permission> {
    private static final String[] PERMISSION_COLUMNS = {
        "Name", "Description", "Status", "Updated At ↓", "Updated By"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("permissionName");
    private static String placeHolderMessage = "Search by Permisison Name";

    public PermissionListPanel(GenericService<Permission> service) {
        super(service, Permission.class, null, PermissionEditDialog.class, PERMISSION_COLUMNS, "Permissions", SEARCH_FIELDS, placeHolderMessage);
        
        // Cấu hình độ rộng cột
        int[] columnWidths = {
            40,    // checkbox
            150,   // Permission Name
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

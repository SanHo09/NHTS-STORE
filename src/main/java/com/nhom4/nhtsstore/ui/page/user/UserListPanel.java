// package com.nhom4.nhtsstore.ui.page.user;

// import com.nhom4.nhtsstore.entities.rbac.User;
// import com.nhom4.nhtsstore.services.EventBus;
// import com.nhom4.nhtsstore.services.GenericService;
// import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
// import java.util.Arrays;
// import java.util.List;
// import javax.swing.SwingConstants;
// import org.springframework.context.annotation.Scope;
// import org.springframework.stereotype.Controller;


// @Scope("prototype")
// @Controller
// public class UserListPanel extends GenericTablePanel<User> {
//    private static final String[] USER_COLUMNS = {
//        "Name", "Role", "Status", "Updated At ↓", "Updated By"
//    };
//    private static final List<String> SEARCH_FIELDS = Arrays.asList("fullName");
//    private static String placeHolderMessage = "Search by User Name";

   
//    public UserListPanel(GenericService<User> service) {
//        super(service, User.class, null, UserEditDialog.class, USER_COLUMNS, "Users", SEARCH_FIELDS, placeHolderMessage);
       
//        // Cấu hình độ rộng cột
//        int[] columnWidths = {
//            40,    // checkbox
//            150,   // FullName
//            150,   // Role
//            80,   // Status
//            150,   // Updated At
//            150    // Updated By
//        };
//        configureColumnWidths(columnWidths);
       
//        setHeaderAlignment(SwingConstants.LEFT);
       
//        EventBus.getReloadSubject().subscribe(isReload -> {
//            if ((Boolean) isReload) {
//                this.loadData();
//                EventBus.postReload(false);
//            }
//        });
//    }
// }

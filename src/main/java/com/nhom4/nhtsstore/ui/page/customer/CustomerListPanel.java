package com.nhom4.nhtsstore.ui.page.customer;

import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.repositories.CustomerRepository;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.services.ICustomerService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class CustomerListPanel extends GenericTablePanel<Customer> {
    private static final String[] CUSTOMER_COLUMNS = {
        "Name", "Email", "Phone Number", "Address", "Status", "Updated At ↓", "Updated By"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("name", "email", "phoneNumber", "address");
    private static String placeHolderMessage = "Search in Name/Email/Phone/Address";

    public CustomerListPanel(ICustomerService customerService) {
        super(customerService, Customer.class, null,null, CustomerEditDialog.class, CUSTOMER_COLUMNS, "Customers", SEARCH_FIELDS, placeHolderMessage);
        
        // Cấu hình độ rộng cột
        int[] columnWidths = {
            40,    // checkbox
            150,   // Name
            180,   // Email
            100,   // Phone Number
            100,   // Address
            60,   // Status
            120,   // Updated At
            120    // Updated By
        };
        
        table.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                configureColumnWidths(columnWidths);
            }
        });
        
        setHeaderAlignment(SwingConstants.LEFT);
        
        EventBus.getReloadSubject().subscribe(isReload -> {
            if ((Boolean) isReload) {
                this.loadData();
                EventBus.postReload(false);
            }
        });
    }
}

package com.nhom4.nhtsstore.ui.page.order;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class OrderListPanel extends GenericTablePanel<Order> {
    private static final String[] ORDER_COLUMNS = {
        "Id", "Customer", "Create Date", "Total Amount", "Status", "Updated At ↓", "Updated By"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("id", "status");
    private static String placeHolderMessage = "Search in Id/Status";

    public OrderListPanel(GenericService<Order> service) {
        super(service, Order.class, OrderEditPanel.class, null, ORDER_COLUMNS, "Orders", SEARCH_FIELDS, placeHolderMessage);
        
        // Cấu hình độ rộng cột
        int[] columnWidths = {
            40,    // checkbox
            50,   // Id
            150,   // Customer
            100,   // Create Date
            100,   // Total Amount
            100,   // Status
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

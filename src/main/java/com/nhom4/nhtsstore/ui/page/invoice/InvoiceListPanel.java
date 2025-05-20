package com.nhom4.nhtsstore.ui.page.invoice;

import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.services.IInvoiceService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class InvoiceListPanel extends GenericTablePanel<Invoice> {
    private static final String[] INVOICE_COLUMNS = {
        "Id", "Total Amount", "Customer", "Create Date ↓"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("id");
    private static String placeHolderMessage = "Search by Id";

    public InvoiceListPanel(IInvoiceService service) {
        super(service, Invoice.class, InvoiceViewPanel.class,null, null, INVOICE_COLUMNS, "Invoices", SEARCH_FIELDS, placeHolderMessage);
        
        // Cấu hình độ rộng cột
        int[] columnWidths = {
            40,   // Id
            250,   // Create Date
            250,   // Total Amount
            250    // Customer
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
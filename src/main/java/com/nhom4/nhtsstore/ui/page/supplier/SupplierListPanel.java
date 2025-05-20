package com.nhom4.nhtsstore.ui.page.supplier;

import com.nhom4.nhtsstore.entities.Supplier;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.services.ISupplierService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class SupplierListPanel extends GenericTablePanel<Supplier> {
   private static final String[] SUPPLIER_COLUMNS = {
       "Name", "Category", "Email", "Address", "Phone Number", "Status", "Updated At ↓", "Updated By"
   };
   private static final List<String> SEARCH_FIELDS = Arrays.asList("name", "address", "email", "phoneNumber");
   private static String placeHolderMessage = "Search by Name/Address/Email/Phone";

   public SupplierListPanel(ISupplierService service) {
       super(service, Supplier.class, null, null,SupplierEditDialog.class, SUPPLIER_COLUMNS, "Suppliers", SEARCH_FIELDS, placeHolderMessage);
       
       // Cấu hình độ rộng cột
       int[] columnWidths = {
           40,    // checkbox
           130,   // Name
           140,   // Category
           150,   // Email
           170,   // Address
           100,   // Phone Number
           60,   // Status
           140,   // Updated At
           100    // Updated By
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

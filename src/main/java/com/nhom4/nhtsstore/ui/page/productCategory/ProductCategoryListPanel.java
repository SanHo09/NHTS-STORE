package com.nhom4.nhtsstore.ui.page.productCategory;

import com.nhom4.nhtsstore.entities.Category;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.services.ICategoryService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
public class ProductCategoryListPanel extends GenericTablePanel<Category> {
    private static final String[] CATEGORY_COLUMNS = {
        "Name", "Status", "Updated At ↓", "Updated By"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("name");
    private static String placeHolderMessage = "Search by Name";

    public ProductCategoryListPanel(ICategoryService service) {
        super(service, Category.class, null,null, ProductCategoryEditDialog.class, CATEGORY_COLUMNS, "Product categories", SEARCH_FIELDS, placeHolderMessage);
        
        // Cấu hình độ rộng cột
        int[] columnWidths = {
            40,    // checkbox
            250,   // Name
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
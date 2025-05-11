package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingConstants;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 *
 * @author NamDang
 */
@Scope("prototype")

@Controller
public class ProductListPanel extends GenericTablePanel<Product> {
    private static final String[] PRODUCT_COLUMNS = {
        "Name", "Sale Price", "Category", "Availability", "Expiry Date", "Status", "Updated At ↓", "Updated By"
    };
    private static final List<String> SEARCH_FIELDS = Arrays.asList("name");

    public ProductListPanel(GenericService<Product> productService) {
        super(productService, Product.class, ProductEditPanel.class, PRODUCT_COLUMNS, "Products", SEARCH_FIELDS);
        
        // Cấu hình độ rộng cột cho ProductPanel
        int[] productColumnWidths = {
            40,    // checkbox
            250,   // Name
            100,   // Sale Price
            150,   // Category
            100,   // Availability
            100,   // Expiry Date
            100,   // Status
            150,   // Updated At
            150    // Updated By
        };
        configureColumnWidths(productColumnWidths);
        
        setHeaderAlignment(SwingConstants.LEFT);
        
        EventBus.getReloadSubject().subscribe(isReload -> {
            if ((Boolean) isReload) {
                this.loadData();
                EventBus.postReload(false);
            }
        });
    }
}

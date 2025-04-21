package com.nhom4.nhtsstore.ui.page.product;

import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.EventBus;
import com.nhom4.nhtsstore.services.GenericService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import org.springframework.stereotype.Controller;

/**
 *
 * @author NamDang
 */
@Controller
public class ProductListPanel extends GenericTablePanel<Product> {
    private static final String[] PRODUCT_COLUMNS = {
        "Name", "SalePrice", "Category", "Quantity", "ExpiryDate", "Status"
    };

    public ProductListPanel(GenericService<Product> productService) {
        super(productService, Product.class, ProductEditPanel.class, PRODUCT_COLUMNS, "Product Management");
        EventBus.getReloadSubject().subscribe(isReload -> {
            if ((Boolean) isReload) {
                this.loadData();
                EventBus.postReload(false);
            }
        });
    }
}

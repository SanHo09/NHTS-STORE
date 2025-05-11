package com.nhom4.nhtsstore.ui.pointOfSale.ActionListener;

import com.nhom4.nhtsstore.entities.Product;

@FunctionalInterface
public interface DeleteCartItemListener {
    void onRemove(Product product);
}

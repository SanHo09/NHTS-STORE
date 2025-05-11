package com.nhom4.nhtsstore.ui.pointOfSale.ActionListener;

import com.nhom4.nhtsstore.entities.Product;

@FunctionalInterface
public interface SetCartQuantityListener {
    void setQuantity(Long productId, int newQuantity);
}

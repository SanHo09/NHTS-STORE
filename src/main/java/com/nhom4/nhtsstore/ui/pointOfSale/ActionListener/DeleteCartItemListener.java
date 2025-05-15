package com.nhom4.nhtsstore.ui.pointOfSale.ActionListener;

import com.nhom4.nhtsstore.viewmodel.cart.CartItemVm;

@FunctionalInterface
public interface DeleteCartItemListener {
    void onRemove(CartItemVm product);
}

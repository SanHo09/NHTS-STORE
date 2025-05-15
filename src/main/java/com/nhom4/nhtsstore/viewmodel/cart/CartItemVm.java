package com.nhom4.nhtsstore.viewmodel.cart;


import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemVm {
    private Long productId;
    private String productName;
    private String manufacturer;
    private int quantity;
    private BigDecimal price;
    private Date addedDate;

    public static OrderDetail toOrderDetail(CartItemVm cartItemVm) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(Product.builder().id(cartItemVm.getProductId()).build());
        orderDetail.setQuantity(cartItemVm.getQuantity());
        orderDetail.setUnitPrice(cartItemVm.getPrice());
        orderDetail.setSubtotal(cartItemVm.getPrice().multiply(BigDecimal.valueOf(cartItemVm.getQuantity())));
        return orderDetail;
    }

}


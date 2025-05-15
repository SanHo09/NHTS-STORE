package com.nhom4.nhtsstore.viewmodel.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartVm {
    private Long userId;
    private List<CartItemVm> items = new ArrayList<>();
    private Date createdDate;
    private Date lastModifiedDate;
    private BigDecimal totalAmount;
}


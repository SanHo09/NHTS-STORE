package com.nhom4.nhtsstore.viewmodel.product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfo {
    private String title;
    private String description;
    private String imageUrl;
    private String imageBase64;

}


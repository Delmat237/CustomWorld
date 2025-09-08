package com.customworld.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
    private String imagePath;
    @Builder.Default
    private boolean isCustomized = false;
}

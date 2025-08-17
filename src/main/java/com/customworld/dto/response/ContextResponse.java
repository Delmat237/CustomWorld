package com.customworld.dto.response;

import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextResponse {
    private List<CategoryResponse> categories;
    private List<ProductResponse> products;
    private CartResponse cart;

    /*
    public ContextResponse(List<ProductResponse> products, CartResponse cart) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
}
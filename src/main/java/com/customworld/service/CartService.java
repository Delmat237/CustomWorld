package com.customworld.service;

import com.customworld.dto.response.CartResponse;

public interface CartService {
    CartResponse getCartByUser(Long userId);
    CartResponse addToCart(Long userId, Long productId, int quantity);
    CartResponse removeFromCart(Long userId, Long cartItemId);
    CartResponse updateCartItemQuantity(Long userId, Long cartItemId, int quantity);
}
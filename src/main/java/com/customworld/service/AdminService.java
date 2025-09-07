package com.customworld.service;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.dto.response.UserResponse;
import com.customworld.enums.UserRole;
import com.customworld.entity.User;


import java.util.List;

public interface AdminService {
    List<User> getAllUsers();
    User createUser(User user);
    List<OrderResponse> getAllOrders();
    OrderResponse assignOrderToDeliverer(Long orderId, Long delivererId);
    ProductResponse validateProduct(Long productId);

    List<UserResponse> getUsersByRole(UserRole role);

    Object getDashboardStatistics();
    void deleteProduct(Long productId);
    void deleteUser(Long userId);
    void updateUser(Long userId,UserRole role);
    void assignDeliveryPerson(Long orderId, Long deliveryPersonId);
    ProductResponse updateProduct(Long productId, ProductRequest productRequest) ;
}
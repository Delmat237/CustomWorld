package com.customworld.service;

import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.User;


import java.util.List;

public interface AdminService {
    List<User> getAllUsers();
    User createUser(User user);
    List<OrderResponse> getAllOrders();
    OrderResponse assignOrderToDeliverer(Long orderId, Long delivererId);
    ProductResponse validateProduct(Long productId);

    <UserResponse> List<UserResponse> getUsersByRole(String role);

    Object getDashboardStatistics();

    void assignDeliveryPerson(Long orderId, Long deliveryPersonId);
}
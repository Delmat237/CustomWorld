package com.customworld.service;

import com.customworld.dto.request.CustomOrderRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CustomOrderRequest orderRequest);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrdersByCustomer(Long customerId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
    OrderResponse assignOrderToDeliverer(Long orderId, Long delivererId);
    void cancelOrder(Long orderId);
}
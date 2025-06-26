package com.customworld.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.customworld.dto.request.CustomOrderRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface du service client
 */
public interface CustomerService {
    /*Page<ProductResponse> getAllAvailableProducts(Pageable pageable);
    List<ProductResponse> getProductsByCategory(String category);
    ProductResponse getProductById(Long id);
    OrderResponse createCustomOrder(CustomOrderRequest request);
    List<OrderResponse> getCustomerOrders();

    OrderResponse getOrderById(Long id);
    */

    void cancelOrder(Long id);

    List<ProductResponse> getAllProducts();

    OrderResponse createOrder(CustomOrderRequest orderRequest);

    String uploadImage(MultipartFile file);

    List<OrderResponse> getOrdersByCustomer(Long customerId);
}
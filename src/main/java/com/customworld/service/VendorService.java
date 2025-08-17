package com.customworld.service;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.customworld.enums.OrderStatus;

import java.util.List;

public interface VendorService {
    ProductResponse createProduct(ProductRequest productRequest);
    String uploadImage(MultipartFile file);
    List<ProductResponse> getProductsByVendor(Long vendorId);
    List<ProductResponse> getVendorProducts(Pageable pageable);
    ProductResponse updateProduct(Long productId, ProductRequest productRequest);
    void deleteProduct(Long productId);
    List<OrderResponse> getVendorOrders();
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
    Object getVendorStatistics();
    
}
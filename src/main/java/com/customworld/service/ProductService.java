package com.customworld.service;

import com.customworld.dto.response.CategoryResponse;
import com.customworld.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    ProductResponse getProductById(Long productId );
    List<ProductResponse> getProductsByVendor(Long vendorId);
    List<ProductResponse> getVendorProducts(Pageable pageable);
    List<CategoryResponse> getAllCategories();
}
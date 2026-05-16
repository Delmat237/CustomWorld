package com.customworld.service.impl;

import com.customworld.dto.response.CategoryResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.Category;
import com.customworld.entity.Product;
import com.customworld.repository.CategoryRepository;
import com.customworld.repository.ProductLikeRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.ProductReviewRepository;
import com.customworld.service.ProductInteractionService;
import com.customworld.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.customworld.exception.ResourceNotFoundException;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductLikeRepository likeRepository;
    private final ProductReviewRepository reviewRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              ProductLikeRepository likeRepository, ProductReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.likeRepository = likeRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        return convertToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category).stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByVendor(Long vendorId) {
        return productRepository.findByVendorId(vendorId).stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getVendorProducts(Pageable pageable) {
        return productRepository.findAll(pageable).stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .coverImageUrl(category.getCoverImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    private ProductResponse convertToProductResponse(Product product) {
        long likeCount = likeRepository.countByProductId(product.getId());
        long reviewCount = reviewRepository.countByProductId(product.getId());
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .category(product.getCategory().getName())
                .vendorId(product.getVendor().getId())
                .imagePath(product.getImagePath())
                .approved(product.isApproved())
                .isNew(product.isNew())
                .isOnSale(product.isOnSale())
                .color(product.getColor())
                .likeCount(likeCount)
                .reviewCount(reviewCount)
                .rating(ProductInteractionService.calculateRating(likeCount))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}

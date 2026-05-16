package com.customworld.service.impl;

import com.customworld.dto.request.CategoryRequest;
import com.customworld.dto.response.CategoryResponse;
import com.customworld.entity.Category;
import com.customworld.repository.CategoryRepository;
import com.customworld.repository.CustomOrderRepository;
import com.customworld.repository.DeliveryRepository;
import com.customworld.repository.ProductLikeRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.ProductReviewRepository;
import com.customworld.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceImplCategoryTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CustomOrderRepository orderRepository = mock(CustomOrderRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ProductLikeRepository likeRepository = mock(ProductLikeRepository.class);
    private final ProductReviewRepository reviewRepository = mock(ProductReviewRepository.class);
    private final AdminServiceImpl adminService = new AdminServiceImpl(
            userRepository,
            orderRepository,
            productRepository,
            deliveryRepository,
            categoryRepository,
            likeRepository,
            reviewRepository
    );

    @Test
    void createCategoryStoresCoverImageUrl() {
        when(categoryRepository.existsByName("Sacs")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(1L);
            return category;
        });
        CategoryRequest request = new CategoryRequest();
        request.setName("Sacs");
        request.setCoverImageUrl("cover-sacs.png");

        CategoryResponse response = adminService.createCategory(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Sacs");
        assertThat(response.getCoverImageUrl()).isEqualTo("cover-sacs.png");
    }

    @Test
    void updateCategoryReplacesCoverImageUrlWhenProvided() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Sacs");
        existing.setCoverImageUrl("old-cover.png");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("Sacs premium")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CategoryRequest request = new CategoryRequest();
        request.setName("Sacs premium");
        request.setCoverImageUrl("new-cover.png");

        CategoryResponse response = adminService.updateCategory(1L, request);

        assertThat(response.getName()).isEqualTo("Sacs premium");
        assertThat(response.getCoverImageUrl()).isEqualTo("new-cover.png");
    }
}

package com.customworld.service.impl;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.Category;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.enums.UserRole;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.CategoryRepository;
import com.customworld.repository.OrderRepository;
import com.customworld.repository.ProductLikeRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.ProductReviewRepository;
import com.customworld.repository.UserRepository;
import com.customworld.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VendorServiceImplProductTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final FileStorageService fileStorageService = mock(FileStorageService.class);
    private final ProductLikeRepository likeRepository = mock(ProductLikeRepository.class);
    private final ProductReviewRepository reviewRepository = mock(ProductReviewRepository.class);

    private final VendorServiceImpl vendorService = new VendorServiceImpl(
            productRepository,
            userRepository,
            categoryRepository,
            orderRepository,
            fileStorageService,
            likeRepository,
            reviewRepository
    );

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vendor@example.com", "password", Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createProductUsesExistingCategory() {
        User vendor = User.builder()
                .id(10L)
                .email("vendor@example.com")
                .role(UserRole.VENDOR)
                .build();
        Category category = Category.builder()
                .id(2L)
                .name("Sacs")
                .build();

        when(userRepository.findByEmail("vendor@example.com")).thenReturn(Optional.of(vendor));
        when(categoryRepository.findByName("Sacs")).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(5L);
            return product;
        });

        ProductResponse response = vendorService.createProduct(productRequest("Sacs"));

        assertEquals(5L, response.getId());
        assertEquals("Sacs", response.getCategory());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createProductRejectsMissingCategory() {
        User vendor = User.builder()
                .id(10L)
                .email("vendor@example.com")
                .role(UserRole.VENDOR)
                .build();

        when(userRepository.findByEmail("vendor@example.com")).thenReturn(Optional.of(vendor));
        when(categoryRepository.findByName("Inconnue")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vendorService.createProduct(productRequest("Inconnue"))
        );

        assertEquals("Catégorie non trouvée", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    private ProductRequest productRequest(String categoryName) {
        return ProductRequest.builder()
                .name("Sac cuir")
                .description("Sac personnalisé")
                .price(120.0)
                .originalPrice(150.0)
                .category(categoryName)
                .imagePath("sac.png")
                .build();
    }
}

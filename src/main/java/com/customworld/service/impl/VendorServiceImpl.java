package com.customworld.service.impl;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.enums.OrderStatus;
import com.customworld.entity.Category;
import com.customworld.entity.Order;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.CategoryRepository;
import com.customworld.repository.OrderRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.UserRepository;
import com.customworld.service.FileStorageService;
import com.customworld.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service d'implémentation pour les opérations liées aux vendeurs.
 * Gère la création, la mise à jour et la suppression des produits,
 * ainsi que les opérations associées aux commandes des vendeurs.
 */
@Service
@Transactional
public class VendorServiceImpl implements VendorService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final FileStorageService fileStorageService;
    private static final Logger log = LoggerFactory.getLogger(VendorServiceImpl.class);

    public VendorServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            OrderRepository orderRepository,
            FileStorageService fileStorageService
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        //Recuperation de l'Utilisateur à partie du token
        User vendor = com.utils.UserInterceptor.getAuthenticatedUser(userRepository);

        Category category = categoryRepository.findByName(productRequest.getCategory())
                .orElseGet(() -> createNewCategory(productRequest.getCategory()));

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .originalPrice(productRequest.getOriginalPrice())
                .category(category)
                .vendor(vendor)
                .imagePath(productRequest.getImagePath())
                .isNew(productRequest.isNew())
                .rating(productRequest.getRating())
                .color(productRequest.getColor())
                .reviews(productRequest.getReviews())
                .isOnSale(productRequest.isOnSale())
                .build();

        product = productRepository.save(product);
        log.info("Product created with ID: {}", product.getId());

        return convertToProductResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        return convertToProductResponse(product);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        log.info("Image uploaded: {}", fileName);
        return fileName;
    }

    @Override
    public List<ProductResponse> getProductsByVendor(Long vendorId) {
        return productRepository.findByVendorId(vendorId).stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getVendorProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found for update: {}", productId);
                    return new ResourceNotFoundException("Produit non trouvé");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Utilisateur non authentifié");
            throw new ResourceNotFoundException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Vendor not found with email: {}", email);
                    return new ResourceNotFoundException("Vendeur non trouvé");
                });

        if (!product.getVendor().getId().equals(vendor.getId())) {
            log.error("Unauthorized update attempt on product {} by vendor {}", productId, vendor.getId());
            throw new ResourceNotFoundException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        if (!product.getCategory().getName().equals(productRequest.getCategory())) {
            Category category = categoryRepository.findByName(productRequest.getCategory())
                    .orElseGet(() -> createNewCategory(productRequest.getCategory()));
            product.setCategory(category);
        }

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setOriginalPrice(productRequest.getOriginalPrice());
        product.setImagePath(productRequest.getImagePath());
        product.setNew(productRequest.isNew());
        product.setRating(productRequest.getRating());
        product.setColor(productRequest.getColor());
        product.setReviews(productRequest.getReviews());
        product.setOnSale(productRequest.isOnSale());
        

        product = productRepository.save(product);
        log.info("Product updated: {}", productId);

        return convertToProductResponse(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Attempt to delete non-existing product: {}", productId);
                    return new ResourceNotFoundException("Produit non trouvé");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Utilisateur non authentifié");
            throw new ResourceNotFoundException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Vendor not found with email: {}", email);
                    return new ResourceNotFoundException("Vendeur non trouvé");
                });

        if (!product.getVendor().getId().equals(vendor.getId())) {
            log.error("Unauthorized delete attempt on product {} by vendor {}", productId, vendor.getId());
            throw new ResourceNotFoundException("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        productRepository.deleteById(productId);
        log.info("Product deleted: {}", productId);
    }



    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Utilisateur non authentifié");
            throw new ResourceNotFoundException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Vendor not found with email: {}", email);
                    return new ResourceNotFoundException("Vendeur non trouvé");
                });

        if (!order.getProduct().getVendor().getId().equals(vendor.getId())) {
            log.error("Unauthorized update attempt on order {} by vendor {}", orderId, vendor.getId());
            throw new ResourceNotFoundException("Vous n'êtes pas autorisé à modifier cette commande");
        }

        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        order = orderRepository.save(order);
        log.info("Order status updated: {} to {}", orderId, status);

        return convertToOrderResponse(order);
    }

    @Override
    public Object getVendorStatistics() {
        log.warn("getVendorStatistics() not implemented");
        return new Object();
    }

    private ProductResponse convertToProductResponse(Product product) {
        log.info("Converting product to response: {}", product);
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
                .rating(product.getRating())
                .color(product.getColor())
                .reviews(product.getReviews())
                .isOnSale(product.isOnSale())
                .build();
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .transactionId(order.getTransactionId())
                .status(order.getStatus())
                .build();
    }

    private Category createNewCategory(String categoryName) {
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory = categoryRepository.save(newCategory);
        log.info("New category created: {}", categoryName);
        return newCategory;
    }

}

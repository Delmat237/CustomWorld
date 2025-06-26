package com.customworld.service.impl;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.Category;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.CategoryRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.UserRepository;
import com.customworld.service.FileStorageService;
import com.customworld.service.VendorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final FileStorageService fileStorageService;
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    public VendorServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            FileStorageService fileStorageService
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Crée un nouveau produit pour un vendeur.
     *
     * @param productRequest DTO contenant les informations du produit
     * @return ProductResponse DTO représentant le produit créé
     * @throws ResourceNotFoundException Si le vendeur n'est pas trouvé
     */
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Validation du vendeur
        User vendor = userRepository.findById(productRequest.getVendorId())
                .orElseThrow(() -> {
                    log.error("Vendor not found with ID: {}", productRequest.getVendorId());
                    return new ResourceNotFoundException("Vendeur non trouvé");
                });

        // Gestion de la catégorie (création si nécessaire)
        Category category = categoryRepository.findByName(productRequest.getCategory())
                .orElseGet(() -> createNewCategory(productRequest.getCategory()));

        // Création et sauvegarde du produit
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(category)
                .vendor(vendor)
                .imagePath(productRequest.getImagePath())
                .build();

        product = productRepository.save(product);
        log.info("Product created with ID: {}", product.getId());

        return convertToProductResponse(product);
    }

    /**
     * Téléverse une image pour un produit.
     *
     * @param file Fichier image à téléverser
     * @return Chemin d'accès à l'image téléversée
     */
    @Override
    public String uploadImage(MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        log.info("Image uploaded: {}", fileName);
        return fileName;
    }

    /**
     * Récupère tous les produits d'un vendeur spécifique.
     *
     * @param vendorId ID du vendeur
     * @return Liste des ProductResponse pour ce vendeur
     */
    @Override
    public List<ProductResponse> getProductsByVendor(Long vendorId) {
        return productRepository.findByVendorId(vendorId).stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les produits paginés pour tous les vendeurs.
     *
     * @param pageable Configuration de pagination
     * @return Liste paginée des ProductResponse
     */
    @Override
    public List<ProductResponse> getVendorProducts(Pageable pageable) {
        return (List<ProductResponse>) productRepository.findAll(pageable)
                .map(this::convertToProductResponse);
    }

    /**
     * Met à jour un produit existant.
     *
     * @param productId ID du produit à mettre à jour
     * @param productRequest DTO contenant les nouvelles données
     * @return ProductResponse mis à jour
     * @throws ResourceNotFoundException Si le produit n'est pas trouvé
     */
    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found for update: {}", productId);
                    return new ResourceNotFoundException("Produit non trouvé");
                });

        // Mise à jour de la catégorie si nécessaire
        if (!product.getCategory().getName().equals(productRequest.getCategory())) {
            Category category = categoryRepository.findByName(productRequest.getCategory())
                    .orElseGet(() -> createNewCategory(productRequest.getCategory()));
            product.setCategory(category);
        }

        // Mise à jour des champs
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImagePath(productRequest.getImagePath());

        product = productRepository.save(product);
        log.info("Product updated: {}", productId);

        return convertToProductResponse(product);
    }

    /**
     * Supprime un produit existant.
     *
     * @param productId ID du produit à supprimer
     * @throws ResourceNotFoundException Si le produit n'est pas trouvé
     */
    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            log.error("Attempt to delete non-existing product: {}", productId);
            throw new ResourceNotFoundException("Produit non trouvé");
        }
        productRepository.deleteById(productId);
        log.info("Product deleted: {}", productId);
    }

    // Méthodes à implémenter ultérieurement
    @Override
    public List<OrderResponse> getVendorOrders() {
        log.warn("getVendorOrders() not implemented");
        return List.of();
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        log.warn("updateOrderStatus() not implemented");
        return new OrderResponse();
    }

    @Override
    public Object getVendorStatistics() {
        log.warn("getVendorStatistics() not implemented");
        return new Object();
    }

    /**
     * Convertit une entité Product en DTO ProductResponse.
     *
     * @param product Entité Product à convertir
     * @return ProductResponse DTO converti
     */
    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory().getName())
                .vendorId(product.getVendor().getId())
                .imagePath(product.getImagePath())
                .build();
    }

    /**
     * Crée une nouvelle catégorie.
     *
     * @param categoryName Nom de la nouvelle catégorie
     * @return Category nouvellement créée
     */
    private Category createNewCategory(String categoryName) {
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory = categoryRepository.save(newCategory);
        log.info("New category created: {}", categoryName);
        return newCategory;
    }
}
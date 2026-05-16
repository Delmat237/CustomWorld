package com.customworld.controller;

import com.customworld.dto.request.CategoryRequest;
import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.request.EmailRequest;
import com.customworld.dto.response.ApiResponseWrapper;
import com.customworld.dto.response.CategoryResponse;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.User;
import com.customworld.enums.OrderStatus;
import com.customworld.enums.UserRole;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.service.AdminService;
import com.customworld.service.AuthService;
import com.customworld.service.FileStorageService;
import com.customworld.service.ProductService;
import com.customworld.service.VendorService;
import com.customworld.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour les fonctionnalités d'administration.
 * Fournit des endpoints pour la gestion des utilisateurs, des commandes, des produits et des catégories.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administrateur", description = "Fournit des endpoints pour la gestion des utilisateurs, des commandes, des produits et des catégories.")
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final AuthService authService;
    private final VendorService vendorService;
    private final OrderService orderService;
    private final NotificationController notificationController;
    private final FileStorageService fileStorageService;

    public AdminController(AdminService adminService, ProductService productService, AuthService authService, 
                           VendorService vendorService, OrderService orderService, NotificationController notificationController,
                           FileStorageService fileStorageService) {
        this.adminService = adminService;
        this.productService = productService;
        this.authService = authService;
        this.vendorService = vendorService;
        this.orderService = orderService;
        this.notificationController = notificationController;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/users")
    @Operation(summary = "Récupère la liste de tous les utilisateurs du système")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<User>>> getAllUsers() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.getAllUsers()));
    }

    @PostMapping("/users")
    @Operation(summary = "Inscription d'un utilisateur par un admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ApiResponseWrapper>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return Mono.fromSupplier(() -> {
            authService.register(registerRequest);
            return ResponseEntity.ok(new ApiResponseWrapper(true, "Utilisateur créé avec succès"));
        });
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Supprime un utilisateur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long userId) {
        return Mono.fromRunnable(() -> adminService.deleteUser(userId))
                .thenReturn(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "Met à jour le rôle d’un utilisateur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> updateUser(@PathVariable Long userId, @RequestBody UserRole role) {
        return Mono.fromRunnable(() -> adminService.updateUser(userId, role))
                .thenReturn(ResponseEntity.ok().<Void>build());
    }

    @GetMapping("/orders")
    @Operation(summary = "Récupère la liste de toutes les commandes")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<OrderResponse>>> getAllOrders() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(orderService.getAllOrders()));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Récupère une commande spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de commande invalide ou commande non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ApiResponseWrapper>> getOrderById(@PathVariable Long orderId) {
        return Mono.fromSupplier(() -> {
            try {
                OrderResponse orderResponse = orderService.getOrderById(orderId);
                String itemsDescription = orderResponse.getItems().stream()
                        .map(item -> item.getProductName() + " (ID: " + item.getProductId() + ", Personnalisé: " + item.isCustomized() + ")")
                        .collect(Collectors.joining("\n"));

                EmailRequest emailRequest = new EmailRequest();
                emailRequest.setEmail( "customworld25@gmail.com");
                emailRequest.setSubject( "Consultation de la commande #" + orderId);
                emailRequest.setMessage("Commande #" + orderId + " consultée.\n" +
                        "Statut: " + orderResponse.getStatus() + "\n" +
                        "Client ID: " + orderResponse.getCustomerId() + "\n" +
                        "Articles:\n" + itemsDescription + "\n" +
                        "Montant: " + orderResponse.getAmount() + " " + orderResponse.getCurrency());
                notificationController.sendEmail(emailRequest);

                return ResponseEntity.ok(new ApiResponseWrapper(true, "Commande récupérée avec succès", orderResponse));
            } catch (ResourceNotFoundException | IllegalStateException e) {
                return ResponseEntity.badRequest().body(new ApiResponseWrapper(false, e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(new ApiResponseWrapper(false, "Erreur serveur"));
            }
        });
    }

    @GetMapping("/orders/customer/{customerId}")
    @Operation(summary = "Récupère la liste des commandes passées par un client spécifique")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<OrderResponse>>> getCustomerOrders(@PathVariable Long customerId) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(orderService.getOrdersByCustomer(customerId)));
    }

    @PutMapping("/orders/{id}/validate")
    @Operation(summary = "Met à jour le statut d’une commande")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<OrderResponse>> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(orderService.updateOrderStatus(id, status)));
    }

    @PutMapping("/orders/{id}/assign")
    @Operation(summary = "Assigne une commande à un livreur spécifique")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<OrderResponse>> assignOrderToDeliverer(@PathVariable Long id, @RequestParam Long delivererId) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(orderService.assignOrderToDeliverer(id, delivererId)));
    }

    @PutMapping("/products/{id}/validate")
    @Operation(summary = "Valide un produit")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ProductResponse>> validateProduct(@PathVariable Long id) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.validateProduct(id)));
    }

    @GetMapping("/products")
    @Operation(summary = "Récupère la liste des produits")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<ProductResponse>>> getProducts() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(productService.getAllProducts()));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Récupère un produit spécifique par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ProductResponse>> getProductById(@PathVariable Long id) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(vendorService.getProductById(id)));
    }

    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Supprime un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long productId) {
        return Mono.fromRunnable(() -> adminService.deleteProduct(productId))
                .thenReturn(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/products/{productId}")
    @Operation(summary = "Met à jour les informations d’un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ProductResponse>> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.updateProduct(productId, productRequest)));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Récupère les statistiques")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Object>> getVendorStatistics() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.getDashboardStatistics()));
    }

    @PostMapping(value = "/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crée une nouvelle catégorie sans fichier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Nom de catégorie déjà existant ou invalide")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.createCategory(categoryRequest)));
    }

    @PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crée une nouvelle catégorie avec une image de couverture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Nom de catégorie déjà existant, invalide ou fichier invalide")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<CategoryResponse>> createCategoryWithCoverImage(
            @Valid @RequestPart("request") CategoryRequest categoryRequest,
            @RequestPart("coverImage") FilePart coverImage) {
        return fileStorageService.storeFile(coverImage)
                .map(fileName -> {
                    categoryRequest.setCoverImageUrl(fileName);
                    return ResponseEntity.ok(adminService.createCategory(categoryRequest));
                });
    }

    @GetMapping("/categories")
    @Operation(summary = "Récupère la liste de toutes les catégories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<CategoryResponse>>> getAllCategories() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.getAllCategories()));
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Récupère une catégorie spécifique par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.getCategoryById(id)));
    }

    @PutMapping(value = "/categories/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Met à jour une catégorie existante sans fichier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Nom de catégorie déjà existant ou invalide"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(adminService.updateCategory(id, categoryRequest)));
    }

    @PutMapping(value = "/categories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Met à jour une catégorie existante avec une image de couverture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Nom de catégorie déjà existant, invalide ou fichier invalide"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<CategoryResponse>> updateCategoryWithCoverImage(
            @PathVariable Long id,
            @Valid @RequestPart("request") CategoryRequest categoryRequest,
            @RequestPart("coverImage") FilePart coverImage) {
        return fileStorageService.storeFile(coverImage)
                .map(fileName -> {
                    categoryRequest.setCoverImageUrl(fileName);
                    return ResponseEntity.ok(adminService.updateCategory(id, categoryRequest));
                });
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Supprime une catégorie existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteCategory(@PathVariable Long id) {
        return Mono.fromRunnable(() -> adminService.deleteCategory(id))
                .thenReturn(ResponseEntity.ok().<Void>build());
    }
}

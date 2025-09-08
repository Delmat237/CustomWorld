package com.customworld.controller;

import com.customworld.dto.request.CategoryRequest;
import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.ApiResponseWrapper;
import com.customworld.dto.response.CategoryResponse;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.User;
import com.customworld.enums.OrderStatus;
import com.customworld.enums.UserRole;
import com.customworld.service.AdminService;
import com.customworld.service.AuthService;
import com.customworld.service.ProductService;
import com.customworld.service.VendorService;
import com.customworld.service.CustomerService;
import com.utils.UserInterceptor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final CustomerService customerService;
    /**
     * Injection du service d'administration via le constructeur.
     * @param adminService service métier pour la gestion admin
     * @param productService service métier pour la gestion des produits
     */
    public AdminController(AdminService adminService, ProductService productService, AuthService authService, VendorService vendorService,
                CustomerService customerService) {
        this.adminService = adminService;
        this.productService = productService;
        this.authService = authService;
        this.vendorService = vendorService;
        this.customerService = customerService;
    }

    /**
     * Endpoint : GET /api/admin/users
     * Description : Récupère la liste de tous les utilisateurs du système.
     * @return Liste des utilisateurs
     */
    @GetMapping("/users")
    @Operation(summary = "Récupère la liste de tous les utilisateurs du système.")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Inscrit un nouvel utilisateur et envoie un email de bienvenue.
     *
     * @param registerRequest Objet contenant les informations d'inscription (nom, email, mot de passe, etc.).
     * @return ResponseEntity contenant un message de succès.
     */
    @Operation(summary = "Inscription d'un utilisateur par un user", description = "Crée un nouveau compte utilisateur et envoie un email de bienvenue.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
    })
    @PostMapping("/users")
    public ResponseEntity<ApiResponseWrapper> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new ApiResponseWrapper(true, "Utilisateur créé avec succès"));
    }

    /**
     * DELETE /api/admin/user/{userId}
     * Supprime un utilisateur existant.
     *
     * @param userId Identifiant du user à supprimer.
     * @return ResponseEntity sans contenu.
     */
    @Operation(summary = "Supprime un utilisateur existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/admin/users/{userId}
     * Met à jour les informations d’un User existant.
     *
     * @param userId Identifiant du produit à mettre à jour.
     * @param role Nouvelles role
     * @return 
     */
    @Operation(summary = "Met à jour le role d’un utilisateur existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long userId,
            @RequestBody UserRole role) {
        adminService.updateUser(userId, role);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint : GET /api/admin/orders
     * Description : Récupère la liste de toutes les commandes.
     * @return Liste des commandes sous forme de DTO OrderResponse
     */
    @Operation(summary = "Récupère la liste de toutes les commandes.")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    
    /**
     * GET /api/admin/orders?customerId={customerId}
     * Récupère la liste des commandes passées par un client spécifique.
     *
     * @param customerId Identifiant du client.
     * @return ResponseEntity contenant la liste des commandes (OrderResponse).
     */
    @Operation(summary = "Récupère la liste des commandes passées par un client spécifique.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable Long customerId) {
        
        return ResponseEntity.ok(customerService.getOrdersByCustomer(customerId));
    }

    /**
     * PUT /api/admin/orders/{id}/validate?status={status}
     * Met à jour le statut d’une commande (ex: VALIDATED, SHIPPED).
     * @param id Identifiant de la commande à mettre à jour.
     * @param status Nouveau statut de la commande.
     * @return Commande mise à jour sous forme de DTO OrderResponse.
     */
    @Operation(summary = "Met à jour le statut d’une commande (ex: VALIDATED, SHIPPED,PAID).")
    @PutMapping("/orders/{id}/validate")        
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus  status) {
        return ResponseEntity.ok(adminService.updateOrderStatus(orderId, status));
    }

    /**
     * Endpoint : PUT /api/admin/orders/{id}/assign?delivererId={delivererId}
     * Description : Assigne une commande à un livreur spécifique.
     * @param id Identifiant de la commande à assigner
     * @param delivererId Identifiant du livreur
     * @return Commande mise à jour sous forme de DTO OrderResponse
     */
    @Operation(summary = "Assigne une commande à un livreur spécifique.")
    @PutMapping("/orders/{id}/assign")
    public ResponseEntity<OrderResponse> assignOrderToDeliverer(@PathVariable Long id, @RequestParam Long delivererId) {
        return ResponseEntity.ok(adminService.assignOrderToDeliverer(id, delivererId));
    }

    /**
     * Endpoint : PUT /api/admin/products/{id}/validate
     * Description : Valide un produit (par exemple, pour publication ou mise en vente).
     * @param id Identifiant du produit à valider
     * @return Produit validé sous forme de DTO ProductResponse
     */
    @Operation(summary = "Valide un produit (par exemple, pour publication ou mise en vente).")
    @PutMapping("/products/{id}/validate")
    public ResponseEntity<ProductResponse> validateProduct(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.validateProduct(id));
    }

    /**
     * GET /api/admin/products
     * Récupère la liste des produits
     *
     * @return Liste des produits sous forme de ProductResponse.
     */
    @Operation(summary = "Récupère la liste des produits.")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /api/admin/products/{id}
     * Récupère un produit spécifique par son identifiant.
     *
     * @param id Identifiant du produit.
     * @return Produit sous forme de ProductResponse.
     */
    @Operation(summary = "Récupère un produit spécifique par son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getProductById(id));
    }

    /**
     * DELETE /api/admin/products/{productId}
     * Supprime un produit existant.
     *
     * @param productId Identifiant du produit à supprimer.
     * @return ResponseEntity sans contenu.
     */
    @Operation(summary = "Supprime un produit existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        adminService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/admin/products/{productId}
     * Met à jour les informations d’un produit existant.
     *
     * @param productId Identifiant du produit à mettre à jour.
     * @param productRequest Nouvelles données du produit.
     * @return Produit mis à jour sous forme de ProductResponse.
     */
    @Operation(summary = "Met à jour les informations d’un produit existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(adminService.updateProduct(productId, productRequest));
    }

    /**
     * GET /api/admin/statistics
     * Récupère les statistiques liées
     *
     * @return Objet contenant les statistiques.
     */
    @Operation(summary = "Récupère les statistiques")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    })
    @GetMapping("/statistics")
    public ResponseEntity<Object> getVendorStatistics() {
        return ResponseEntity.ok(adminService.getDashboardStatistics());
    }

    /**
     * POST /api/admin/categories
     * Crée une nouvelle catégorie.
     *
     * @param categoryRequest Nom de la nouvelle catégorie.
     * @return Catégorie créée sous forme de CategoryResponse.
     */
    @Operation(summary = "Crée une nouvelle catégorie", description = "Ajoute une nouvelle catégorie au système.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Nom de catégorie déjà existant ou invalide")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(adminService.createCategory(categoryRequest));
    }

    /**
     * GET /api/admin/categories
     * Récupère la liste de toutes les catégories.
     *
     * @return Liste des catégories sous forme de CategoryResponse.
     */
    @Operation(summary = "Récupère la liste de toutes les catégories", description = "Retourne toutes les catégories disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    /**
     * GET /api/admin/categories/{id}
     * Récupère une catégorie spécifique par son identifiant.
     *
     * @param id Identifiant de la catégorie.
     * @return Catégorie sous forme de CategoryResponse.
     */
    @Operation(summary = "Récupère une catégorie spécifique par son identifiant", description = "Retourne les détails d'une catégorie spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCategoryById(id));
    }

    /**
     * PUT /api/admin/categories/{id}
     * Met à jour une catégorie existante.
     *
     * @param id Identifiant de la catégorie à mettre à jour.
     * @param categoryRequest Nouvelles données de la catégorie.
     * @return Catégorie mise à jour sous forme de CategoryResponse.
     */
    @Operation(summary = "Met à jour une catégorie existante", description = "Modifie le nom d'une catégorie existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Nom de catégorie déjà existant ou invalide"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(adminService.updateCategory(id, categoryRequest));
    }

    /**
     * DELETE /api/admin/categories/{id}
     * Supprime une catégorie existante.
     *
     * @param id Identifiant de la catégorie à supprimer.
     * @return ResponseEntity sans contenu.
     */
    @Operation(summary = "Supprime une catégorie existante", description = "Supprime une catégorie du système.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

}

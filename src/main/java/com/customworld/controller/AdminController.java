package com.customworld.controller;

import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.ApiResponseWrapper;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.User;
import com.customworld.service.AdminService;
import com.customworld.service.AuthService;
import com.customworld.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;




import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour les fonctionnalités d'administration.
 * Fournit des endpoints pour la gestion des utilisateurs, des commandes et des produits.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administrateur", description = "Fournit des endpoints pour la gestion des utilisateurs, des commandes et des produits.")
public class AdminController {

    private final AdminService adminService ;
    private final ProductService productService;
    private final AuthService authService;

    /**
     * Injection du service d'administration via le constructeur. 
     * @param adminService service métier pour la gestion admin
     * @param productService service métier pour la gestion des produits
     */
    public AdminController(AdminService adminService, ProductService productService,AuthService authService) {
        this.adminService = adminService;
        this.productService = productService;
        this.authService = authService;
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
    @Operation(summary = "Inscription d'un utilisateur par un user ", description = "Crée un nouveau compte utilisateur et envoie un email de bienvenue.")
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
    @Operation(summary = "Récupère la liste des produits .")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
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

   
}

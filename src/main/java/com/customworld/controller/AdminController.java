package com.customworld.controller;

import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.User;
import com.customworld.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final AdminService adminService;

    /**
     * Injection du service d'administration via le constructeur.
     * @param adminService service métier pour la gestion admin
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
     * Endpoint : POST /api/admin/users
     * Description : Crée un nouvel utilisateur dans le système.
     * @param user Objet utilisateur à créer (donné dans le corps de la requête)
     * @return Utilisateur créé
     */
    @PostMapping("/users")
    @Operation(summary = "Crée un nouvel utilisateur dans le système.")
    public ResponseEntity<User> createUser(@RequestBody User user) {

            return ResponseEntity.ok( adminService.createUser(user));


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
}

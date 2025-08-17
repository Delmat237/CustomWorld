package com.customworld.controller;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.customworld.enums.OrderStatus;
import java.util.List;

/**
 * Contrôleur REST dédié aux opérations des vendeurs.
 * Permet la gestion des produits, des commandes et la consultation des statistiques.
 */
@RestController
@RequestMapping("/api/vendor")
@Tag(name ="Vendeur", description = "Permet la gestion des produits, des commandes et la consultation des statistiques.")
public class VendorController {

    private final VendorService vendorService;

    /**
     * Constructeur avec injection du service vendeur.
     * @param vendorService Service métier pour la gestion des opérations vendeurs.
     */
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * POST /api/vendor/products
     * Crée un nouveau produit pour le vendeur.
     *
     * @param productRequest Données du produit à créer.
     * @return Produit créé sous forme de ProductResponse.
     */
    @PostMapping("/products")
    @Operation(summary = "Crée un nouveau produit pour le vendeur.")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(vendorService.createProduct(productRequest));
    }

    /**
     * POST /api/vendor/products/image
     * Upload d'une image associée à un produit.
     *
     * @param file Fichier image envoyé via multipart/form-data.
     * @return Chemin ou URL de l'image uploadée.
     */
    @Operation(summary = "Upload d'une image associée à un produit.")
    @PostMapping("/products/image")
    public ResponseEntity<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(vendorService.uploadImage(file));
    }

    /**
     * GET /api/vendor/products?vendorId={vendorId}
     * Récupère la liste des produits d’un vendeur spécifique.
     *
     * @param vendorId Identifiant du vendeur.
     * @return Liste des produits sous forme de ProductResponse.
     */
    @Operation(summary = "Récupère la liste des produits d’un vendeur spécifique.")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getVendorProducts(@RequestParam Long vendorId) {
        return ResponseEntity.ok(vendorService.getProductsByVendor(vendorId));
    }

    /**
     * GET /api/vendor/products/paged
     * Récupère la liste paginée des produits du vendeur.
     *
     * @param pageable Paramètres de pagination (page, taille, tri).
     * @return Liste paginée des produits.
     */
    @Operation(summary = "Récupère la liste paginée des produits du vendeur.")
    @GetMapping("/products/paged")
    public ResponseEntity<List<ProductResponse>> getVendorProductsPaged(Pageable pageable) {
        return ResponseEntity.ok(vendorService.getVendorProducts(pageable));
    }

    /**
     * PUT /api/vendor/products/{productId}
     * Met à jour les informations d’un produit existant.
     *
     * @param productId Identifiant du produit à mettre à jour.
     * @param productRequest Nouvelles données du produit.
     * @return Produit mis à jour sous forme de ProductResponse.
     */
    @Operation(summary = "Met à jour les informations d’un produit existant.")
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(vendorService.updateProduct(productId, productRequest));
    }

    /**
     * DELETE /api/vendor/products/{productId}
     * Supprime un produit existant.
     *
     * @param productId Identifiant du produit à supprimer.
     * @return ResponseEntity sans contenu.
     */
    @Operation(summary = "Supprime un produit existant.")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        vendorService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/vendor/orders
     * Récupère la liste des commandes associées au vendeur.
     *
     * @return Liste des commandes sous forme de OrderResponse.
     */
    @Operation(summary = "Récupère la liste des commandes associées au vendeur.")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getVendorOrders() {
        return ResponseEntity.ok(vendorService.getVendorOrders());
    }

    /**
     * PUT /api/vendor/orders/{orderId}/status?status={status}
     * Met à jour le statut d’une commande.
     *
     * @param orderId Identifiant de la commande.
     * @param status Nouveau statut de la commande.
     * @return Commande mise à jour sous forme de OrderResponse.
     */
    @Operation(summary = "Met à jour le statut d’une commande.")
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(vendorService.updateOrderStatus(orderId, status));
    }

    /**
     * GET /api/vendor/statistics
     * Récupère les statistiques liées au vendeur (ventes, produits, etc.).
     *
     * @return Objet contenant les statistiques du vendeur.
     */
    @GetMapping("/statistics")
    @Operation(summary = "Récupère les statistiques liées au vendeur (ventes, produits, etc.).")
    public ResponseEntity<Object> getVendorStatistics() {
        return ResponseEntity.ok(vendorService.getVendorStatistics());
    }
}

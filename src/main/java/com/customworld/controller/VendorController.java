package com.customworld.controller;

import com.customworld.dto.request.ProductRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Vendeur", description = "Permet la gestion des produits, des commandes et la consultation des statistiques.")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de la requête invalides"),
            @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    })
    @PreAuthorize("hasRole('VENDOR')")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploadée avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide")
    })
    @PreAuthorize("hasRole('VENDOR')")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée"),
            @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    })
    @PreAuthorize("hasRole('VENDOR')")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getVendorProducts(@RequestParam Long vendorId) {
        return ResponseEntity.ok(vendorService.getProductsByVendor(vendorId));
    }

    /**
     * GET /api/vendor/products/{id}
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
    @PreAuthorize("hasRole('VENDOR')")
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getProductById(id));
    }

    /**
     * GET /api/vendor/products/paged
     * Récupère la liste paginée des produits du vendeur.
     *
     * @param pageable Paramètres de pagination (page, taille, tri).
     * @return Liste paginée des produits.
     */
    @Operation(summary = "Récupère la liste paginée des produits du vendeur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste paginée récupérée"),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides")
    })
    @PreAuthorize("hasRole('VENDOR')")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PreAuthorize("hasRole('VENDOR')")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PreAuthorize("hasRole('VENDOR')")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        vendorService.deleteProduct(productId);
        return ResponseEntity.ok().build();
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut de la commande mis à jour"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @PreAuthorize("hasRole('VENDOR')")
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
    @Operation(summary = "Récupère les statistiques liées au vendeur (ventes, produits, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    })
    @PreAuthorize("hasRole('VENDOR')")
    @GetMapping("/statistics")
    public ResponseEntity<Object> getVendorStatistics() {
        return ResponseEntity.ok(vendorService.getVendorStatistics());
    }
}
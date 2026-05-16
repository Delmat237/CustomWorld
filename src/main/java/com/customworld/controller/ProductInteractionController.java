package com.customworld.controller;

import com.customworld.dto.request.ReviewRequest;
import com.customworld.dto.response.ReviewResponse;
import com.customworld.service.ProductInteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Interactions Produit", description = "Gestion des likes et avis sur les produits")
public class ProductInteractionController {

    private final ProductInteractionService interactionService;

    public ProductInteractionController(ProductInteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/{productId}/like")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Liker ou unliker un produit (toggle). Retourne le nouvel état et le nombre total de likes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action effectuée"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public Mono<ResponseEntity<Map<String, Object>>> toggleLike(@PathVariable Long productId) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(interactionService.toggleLike(productId)));
    }

    @GetMapping("/{productId}/likes")
    @Operation(summary = "Récupère le nombre de likes d'un produit")
    public Mono<ResponseEntity<Map<String, Long>>> getLikeCount(@PathVariable Long productId) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(Map.of("likeCount", interactionService.getLikeCount(productId))));
    }

    @GetMapping("/{productId}/likes/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Vérifie si l'utilisateur connecté a liké ce produit")
    public Mono<ResponseEntity<Map<String, Boolean>>> hasLiked(@PathVariable Long productId) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(Map.of("liked", interactionService.hasCurrentUserLiked(productId))));
    }

    @PostMapping("/{productId}/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Soumettre un avis/témoignage sur un produit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avis soumis avec succès"),
            @ApiResponse(responseCode = "400", description = "Contenu invalide"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public Mono<ResponseEntity<ReviewResponse>> addReview(@PathVariable Long productId,
                                                           @RequestBody @Valid ReviewRequest request) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(interactionService.addReview(productId, request)));
    }

    @GetMapping("/{productId}/reviews")
    @Operation(summary = "Récupère tous les avis d'un produit (du plus récent au plus ancien)")
    public Mono<ResponseEntity<List<ReviewResponse>>> getReviews(@PathVariable Long productId) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(interactionService.getProductReviews(productId)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Supprimer son propre avis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avis supprimé"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    })
    public Mono<ResponseEntity<Void>> deleteReview(@PathVariable Long reviewId) {
        return Mono.fromRunnable(() -> interactionService.deleteReview(reviewId))
                .thenReturn(ResponseEntity.ok().<Void>build());
    }
}

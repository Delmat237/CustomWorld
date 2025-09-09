package com.customworld.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un article d'une commande.
 * Contient les informations d'un article pour la réponse au client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private String imagePath;
    private boolean isCustomized;
    private int quantity;
}
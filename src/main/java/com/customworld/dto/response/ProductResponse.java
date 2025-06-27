package com.customworld.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant la réponse d'un produit.
 * Contient les informations principales d'un produit pour la communication avec le client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Long vendorId;
    private String imagePath;
    private boolean approved;


}

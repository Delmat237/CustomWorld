package com.customworld.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'une commande à partir du panier.
 * Contient les informations de livraison nécessaires.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationRequest {
    private String deliveryAddress;
    private Long modeLivraison;
    private String phone;
}
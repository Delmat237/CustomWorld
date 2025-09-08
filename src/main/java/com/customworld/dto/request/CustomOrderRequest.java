package com.customworld.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant une requête de création de commande personnalisée.
 * Contient les informations nécessaires pour créer une commande client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomOrderRequest {

    private Long customerId;
    private Long productId;
    private String deliveryAddress;
    private String imagePath;
    private Long modeLivraison;
    private String phone;


}

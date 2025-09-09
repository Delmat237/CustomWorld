package com.customworld.dto.response;

import java.time.LocalDateTime;

import com.customworld.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant la réponse d'une commande.
 * Contient les informations principales d'une commande pour la communication avec le client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long customerId;
    private Long productId;
    private String productName;
    private String imagePath;
    private String deliveryAddress;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Double amount;
    private String currency;
    private String transactionId;
    private Long modeLivraison;
    private String phone;

}

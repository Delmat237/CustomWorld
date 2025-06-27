package com.customworld.dto.response;

import com.customworld.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO représentant la réponse d'une livraison.
 * Contient les informations principales d'une livraison pour la communication avec le client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryResponse {

    private Long id;
    private Long orderId;
    private Long delivererId;
    private DeliveryStatus status;
    private String issueDescription;
    private LocalDateTime deliveryDate;


}

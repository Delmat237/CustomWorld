package com.customworld.dto.response;

import lombok.Data;

/**
 * DTO pour la réponse d'un paiement via CinetPay.
 */
@Data
public class PaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private String paymentUrl; // URL de paiement redirigée
}
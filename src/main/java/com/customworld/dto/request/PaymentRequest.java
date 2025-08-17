package com.customworld.dto.request;

import lombok.Data;

/**
 * DTO pour initier un paiement via CinetPay.
 */
@Data
public class PaymentRequest {
    private String amount;
    private String currency;
    private String description;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String paymentMethod; // Ex. "OMCI" pour Orange Money CI, "MOMO" pour MTN Mobile Money CI
    private Long productId; // ID du produit associé
}
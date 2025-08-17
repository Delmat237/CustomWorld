package com.customworld.controller;

import com.customworld.dto.request.PaymentRequest;
import com.customworld.dto.response.PaymentResponse;
import com.customworld.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Paiements", description = "API pour la gestion des paiements via CinetPay")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Initier un paiement", description = "Crée une transaction de paiement via CinetPay (Orange Money, MTN Mobile Money, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement initié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de la requête invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de l'initiation du paiement")
    })
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
     //   PaymentResponse response = ;
        return ResponseEntity.ok(paymentService.initiatePayment(paymentRequest));
    }

    @Operation(summary = "Notification de paiement", description = "Reçoit les notifications de CinetPay après un paiement.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification reçue"),
            @ApiResponse(responseCode = "400", description = "Données de notification invalides")
    })
    @PostMapping("/notify")
    public ResponseEntity<Void> handlePaymentNotification(@RequestBody Map<String, Object> notification) {
        paymentService.handlePaymentNotification(notification);
        return ResponseEntity.ok().build();
    }
}
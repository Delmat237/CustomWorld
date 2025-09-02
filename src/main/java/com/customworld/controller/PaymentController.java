package com.customworld.controller;

import com.customworld.dto.request.PaymentRequest;
import com.customworld.dto.response.PaymentResponse;
import com.customworld.entity.Order;
import com.customworld.enums.OrderStatus;
import com.customworld.repository.OrderRepository;
import com.customworld.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Paiement", description = "API pour la gestion des paiements via Notch Pay")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @Value("${notchpay.private-key}")
    private String privateKey;

    public PaymentController(OrderRepository orderRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @Operation(summary = "Initier un paiement", description = "Crée une transaction de paiement via Notch Pay (Orange Money, MTN Mobile Money, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement initié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de la requête invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de l'initiation du paiement")
    })
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.initiatePayment(paymentRequest);
        if ("FAILED".equals(response.getStatus())) {
            log.warn("Payment initiation failed: {}", response);
            return ResponseEntity.status(400).body(response); // 400 pour une requête invalide ou un échec
        }
        if (response.getTransaction() == null || response.getAuthorization_url() == null) {
            log.warn("Incomplete payment response: {}", response);
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Callback pour notification de paiement Notch Pay", description = "Gère les notifications de Notch Pay pour mettre à jour le statut des commandes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification reçue et traitée"),
            @ApiResponse(responseCode = "400", description = "Données de notification invalides ou commande non trouvée")
    })
    @PostMapping("/notify")
    public ResponseEntity<String> notifyPayment(@RequestBody Map<String, Object> notification,
                                               @RequestHeader(value = "X-NotchPay-Signature", required = false) String signature) {
        try {
            log.info("Notification Notch Pay reçue: {}", notification);

            if (signature != null) {
                String computedSignature = computeSignature(notification, privateKey);
                if (!signature.equals(computedSignature)) {
                    log.warn("Signature invalide pour la notification: {}", notification);
                    return ResponseEntity.ok("OK");
                }
            }

            String transactionId = (String) notification.get("merchant_reference");
            String event = (String) notification.get("event");

            if (transactionId == null || event == null) {
                log.warn("Données invalides dans la notification: {}", notification);
                return ResponseEntity.ok("OK");
            }

            Order order = orderRepository.findByTransactionId(transactionId)
                    .orElse(null);

            if (order == null) {
                log.warn("Commande non trouvée pour transaction: {}", transactionId);
                return ResponseEntity.ok("OK");
            }

            if ("payment.complete".equals(event)) {
                order.setStatus(OrderStatus.PAID);
                order.setUpdatedAt(java.time.Instant.now());
                orderRepository.save(order);
                log.info("Paiement réussi pour transaction: {}, commande mise à jour à PAID", transactionId);
            } else if ("payment.failed".equals(event)) {
                order.setStatus(OrderStatus.FAILED);
                order.setUpdatedAt(java.time.Instant.now());
                orderRepository.save(order);
                log.warn("Paiement échoué pour transaction: {}, événement: {}", transactionId, event);
            } else {
                log.warn("Événement inconnu pour transaction: {}, événement: {}", transactionId, event);
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Erreur lors du traitement du webhook: {}", e.getMessage(), e);
            return ResponseEntity.ok("OK");
        }
    }

    private String computeSignature(Map<String, Object> notification, String privateKey) {
        try {
            String data = notification.get("reference") + "|" + notification.get("event") + "|" + notification.get("amount");
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((data + privateKey).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Erreur lors du calcul de la signature: {}", e.getMessage());
            return null;
        }
    }
}
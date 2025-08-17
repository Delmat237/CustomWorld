package com.customworld.service;

import com.customworld.dto.request.PaymentRequest;
import com.customworld.dto.response.PaymentResponse;
import com.customworld.entity.Order;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.OrderRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.customworld.enums.OrderStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Value("${cinetpay.api-key}")
    private String apiKey;

    @Value("${cinetpay.site-id}")
    private String siteId;

    @Value("${cinetpay.api-url}")
    private String apiUrl;

    @Value("${cinetpay.notify-url}")
    private String notifyUrl;

    @Value("${cinetpay.return-url}")
    private String returnUrl;

    public PaymentService(RestTemplate restTemplate, ObjectMapper objectMapper,
                         ProductRepository productRepository, UserRepository userRepository,
                         OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        try {
            // Récupérer l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User customer = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

            // Récupérer le produit
            Product product = productRepository.findById(paymentRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

            // Créer une commande
            String transactionId = UUID.randomUUID().toString();
            Order order = Order.builder()
                    .customer(customer)
                    .product(product)
                    .amount(Double.parseDouble(paymentRequest.getAmount()))
                    .currency(paymentRequest.getCurrency())
                    .transactionId(transactionId)
                    .status(OrderStatus.PENDING)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            orderRepository.save(order);

            // Préparer la requête CinetPay
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("apikey", apiKey);
            requestBody.put("site_id", siteId);
            requestBody.put("transaction_id", transactionId);
            requestBody.put("amount", paymentRequest.getAmount());
            requestBody.put("currency", paymentRequest.getCurrency());
            requestBody.put("description", paymentRequest.getDescription());
            requestBody.put("customer_name", paymentRequest.getCustomerName());
            requestBody.put("customer_email", paymentRequest.getCustomerEmail());
            requestBody.put("customer_phone_number", paymentRequest.getCustomerPhone());
            requestBody.put("channels", paymentRequest.getPaymentMethod());
            requestBody.put("notify_url", notifyUrl);
            requestBody.put("return_url", returnUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            logger.debug("Envoi de la requête de paiement à CinetPay : {}", requestBody);
            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            logger.debug("Réponse de CinetPay : {}", response);

            // Analyse de la réponse JSON
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setSuccess(responseMap.get("code").equals("201"));
            paymentResponse.setMessage((String) responseMap.get("message"));
            paymentResponse.setTransactionId(transactionId);
            paymentResponse.setPaymentUrl(data != null ? (String) data.get("payment_url") : null);

            return paymentResponse;
        } catch (Exception e) {
            logger.error("Erreur lors de l'initiation du paiement : {}", e.getMessage());
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setSuccess(false);
            paymentResponse.setMessage("Erreur lors de l'initiation du paiement : " + e.getMessage());
            return paymentResponse;
        }
    }

    @Transactional
    public void handlePaymentNotification(Map<String, Object> notification) {
        String transactionId = (String) notification.get("transaction_id");
        Order order = orderRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée pour transactionId: " + transactionId));

        // Vérifier le statut du paiement avec l'API CinetPay
        // TODO: Implémenter la vérification via l'API de CinetPay
        OrderStatus status = (OrderStatus) notification.get("status"); // Exemple: "SUCCESS" ou "FAILED"
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
        logger.info("Notification de paiement traitée pour transactionId: {}, statut: {}", transactionId, status);
    }
}
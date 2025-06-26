package com.customworld.controller;

import com.customworld.dto.request.CustomOrderRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur REST dédié aux opérations liées aux clients.
 * Fournit des endpoints pour consulter les produits, créer des commandes,
 * uploader des images et récupérer les commandes d’un client.
 */
@RestController
@RequestMapping("/api/customer")
@Tag(name = "Client" , description = "Fournit des endpoints pour consulter les produits, créer des commandes, uploader des images et récupérer les commandes d’un client.")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Constructeur avec injection du service client.
     * @param customerService Service métier pour la gestion des opérations clients.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /api/customer/products
     * Récupère la liste de tous les produits disponibles.
     *
     * @return ResponseEntity contenant la liste des produits (ProductResponse).
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(customerService.getAllProducts());
    }

    /**
     * POST /api/customer/orders
     * Crée une nouvelle commande à partir des informations fournies.
     *
     * @param orderRequest Objet contenant les détails de la commande.
     * @return ResponseEntity contenant la commande créée (OrderResponse).
     */
    @PostMapping("/orders")
    @Operation(summary = "Crée une nouvelle commande à partir des informations fournies.")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CustomOrderRequest orderRequest) {
        return ResponseEntity.ok(customerService.createOrder(orderRequest));
    }

    /**
     * POST /api/customer/orders/upload
     * Permet d’uploader une image associée à une commande (ex : photo produit).
     *
     * @param file Fichier image envoyé dans la requête multipart.
     * @return ResponseEntity contenant le chemin ou URL de l’image uploadée.
     */
    @Operation(summary = "Permet d’uploader une image associée à une commande (ex : photo produit).")
    @PostMapping("/orders/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String filePath = customerService.uploadImage(file);
        return ResponseEntity.ok(filePath);
    }

    /**
     * GET /api/customer/orders?customerId={customerId}
     * Récupère la liste des commandes passées par un client spécifique.
     *
     * @param customerId Identifiant du client.
     * @return ResponseEntity contenant la liste des commandes (OrderResponse).
     */
    @Operation(summary = "Récupère la liste des commandes passées par un client spécifique.")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@RequestParam Long customerId) {
        return ResponseEntity.ok(customerService.getOrdersByCustomer(customerId));
    }
}

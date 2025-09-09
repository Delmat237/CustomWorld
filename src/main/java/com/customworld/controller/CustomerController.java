package com.customworld.controller;

import com.customworld.dto.request.OrderCreationRequest;
import com.customworld.dto.request.EmailRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.dto.response.ContextResponse;
import com.customworld.dto.response.ApiResponseWrapper;
import com.customworld.dto.response.CartResponse;
import com.customworld.dto.response.CategoryResponse;
import com.customworld.service.CustomerService;
import com.customworld.service.CartService;
import com.customworld.service.ProductService;
import com.customworld.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.customworld.repository.UserRepository;
import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;
import com.utils.UserInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contrôleur REST dédié aux opérations liées aux clients.
 * Fournit des endpoints pour consulter les produits, créer des commandes,
 * uploader des images, récupérer les commandes d’un client et gérer le panier.
 */
@RestController
@RequestMapping("/api/customer")
@Tag(name = "Client", description = "Fournit des endpoints pour consulter les produits, créer des commandes, uploader des images, récupérer les commandes d’un client et gérer le panier.")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final NotificationController notificationController;

    public CustomerController(CustomerService customerService, CartService cartService, 
                             ProductService productService, OrderService orderService,
                             UserRepository userRepository, NotificationController notificationController) {
        this.customerService = customerService;
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.notificationController = notificationController;
    }

    @GetMapping("/products")
    @Operation(summary = "Récupère la liste de tous les produits disponibles")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/products/by-category")
    @Operation(summary = "Récupère les produits par catégorie")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Récupère un produit spécifique par son identifiant")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/cart")
    @Operation(summary = "Récupère le panier d’un client")
    public ResponseEntity<CartResponse> getCart() {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    @PostMapping("/cart/add")
    @Operation(summary = "Ajoute un produit au panier")
    public ResponseEntity<CartResponse> addToCart(@RequestParam Long productId, @RequestParam int quantity, @RequestParam boolean isCustomized) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity, isCustomized));
    }

    @DeleteMapping("/cart/remove/{cartItemId}")
    @Operation(summary = "Supprime un article du panier")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long cartItemId) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.removeFromCart(userId, cartItemId));
    }

    @PutMapping("/cart/update/{cartItemId}")
    @Operation(summary = "Met à jour la quantité d’un article dans le panier")
    public ResponseEntity<CartResponse> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam int quantity) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, cartItemId, quantity));
    }

    @PostMapping("/orders")
    @Operation(summary = "Crée une nouvelle commande à partir du panier de l'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Panier vide ou utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<ApiResponseWrapper> createOrder(@RequestBody OrderCreationRequest orderRequest) {
        try {
            User user = UserInterceptor.getAuthenticatedUser(userRepository);
            OrderResponse orderResponse = orderService.createOrderFromCart(user.getId(), orderRequest);

            String itemsDescription = orderResponse.getItems().stream()
                    .map(item -> item.getProductName() + " (ID: " + item.getProductId() + ", Personnalisé: " + item.isCustomized() + ")")
                    .collect(Collectors.joining("\n"));

           EmailRequest emailRequest = new EmailRequest();
            emailRequest.setEmail( user.getEmail());
            emailRequest.setSubject( "Confirmation de votre commande #" + orderResponse.getId());
            emailRequest.setMessage( "Bonjour " + user.getName() + ",\n\n" +
                    "Votre commande #" + orderResponse.getId() + " a été créée avec succès.\n" +
                    "Statut: " + orderResponse.getStatus() + "\n" +
                    "Adresse de livraison: " + orderResponse.getDeliveryAddress() + "\n" +
                    "Articles:\n" + itemsDescription + "\n" +
                    "Montant: " + orderResponse.getAmount() + " " + orderResponse.getCurrency() + "\n" +
                    "Merci de faire confiance à Custom World !");
            notificationController.sendEmail(emailRequest);

            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                Map<String, String> smsRequest = new HashMap<>();
                smsRequest.put("phone", user.getPhone());
                smsRequest.put("message", "Commande #" + orderResponse.getId() + " créée. Statut: " + orderResponse.getStatus());
                notificationController.sendSms(smsRequest);
            }

            return ResponseEntity.ok(new ApiResponseWrapper(true, "Commande créée avec succès", orderResponse));
        } catch (ResourceNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponseWrapper(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création de la commande: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponseWrapper(false, "Erreur serveur"));
        }
    }

    @PostMapping("/orders/upload")
    @Operation(summary = "Permet d’uploader une image associée à une commande")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String filePath = customerService.uploadImage(file);
        return ResponseEntity.ok(filePath);
    }

    @GetMapping("/orders")
    @Operation(summary = "Récupère la liste des commandes passées par un client authentifié")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders() {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long customerId = user.getId();
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Récupère une commande spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de commande invalide ou commande non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<ApiResponseWrapper> getOrderById(@PathVariable Long orderId) {
        try {
            User user = UserInterceptor.getAuthenticatedUser(userRepository);
            OrderResponse orderResponse = orderService.getOrderById(orderId);
            if (!orderResponse.getCustomerId().equals(user.getId())) {
                return ResponseEntity.status(403)
                        .body(new ApiResponseWrapper(false, "Accès non autorisé à cette commande"));
            }

            String itemsDescription = orderResponse.getItems().stream()
                    .map(item -> item.getProductName() + " (ID: " + item.getProductId() + ", Personnalisé: " + item.isCustomized() + ")")
                    .collect(Collectors.joining("\n"));

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setEmail( user.getEmail());
            emailRequest.setSubject( "Détails de votre commande #" + orderId);
            emailRequest.setMessage( "Bonjour " + user.getName() + ",\n\n" +
                    "Vous avez consulté les détails de votre commande #" + orderId + ".\n" +
                    "Statut: " + orderResponse.getStatus() + "\n" +
                    "Adresse de livraison: " + orderResponse.getDeliveryAddress() + "\n" +
                    "Articles:\n" + itemsDescription + "\n" +
                    "Montant: " + orderResponse.getAmount() + " " + orderResponse.getCurrency() + "\n" +
                    "Merci de faire confiance à Custom World !");
            notificationController.sendEmail(emailRequest);

            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                Map<String, String> smsRequest = new HashMap<>();
                smsRequest.put("phone", user.getPhone());
                smsRequest.put("message", "Commande #" + orderId + " consultée. Statut: " + orderResponse.getStatus());
                notificationController.sendSms(smsRequest);
            }

            return ResponseEntity.ok(new ApiResponseWrapper(true, "Commande récupérée avec succès", orderResponse));
        } catch (ResourceNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponseWrapper(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la commande {}: {}", orderId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponseWrapper(false, "Erreur serveur"));
        }
    }

    @GetMapping("/context")
    @Operation(summary = "Récupère les catégories, les produits et le panier du client")
    public ResponseEntity<ContextResponse> getCustomerContext(@RequestParam(required = false) String category) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        List<CategoryResponse> categories = productService.getAllCategories();
        List<ProductResponse> products = category != null
                ? productService.getProductsByCategory(category)
                : productService.getAllProducts();
        CartResponse cart = cartService.getCartByUser(userId);

        return ResponseEntity.ok(new ContextResponse(categories, products, cart));
    }

    @GetMapping("/categories")
    @Operation(summary = "Récupère la liste des catégories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @DeleteMapping("/cart/clear")
    @Operation(summary = "Vider le panier")
    public ResponseEntity<CartResponse> clearCart() {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}
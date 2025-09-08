package com.customworld.controller;

import com.customworld.dto.request.CustomOrderRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.dto.response.ContextResponse;
import com.customworld.dto.response.CartResponse;
import com.customworld.dto.response.CategoryResponse;
import com.customworld.service.CustomerService;
import com.customworld.service.CartService;
import com.customworld.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.customworld.repository.UserRepository;
import com.customworld.entity.User;
import com.utils.UserInterceptor;

import java.util.List;

/**
 * Contrôleur REST dédié aux opérations liées aux clients.
 * Fournit des endpoints pour consulter les produits, créer des commandes,
 * uploader des images, récupérer les commandes d’un client et gérer le panier.
 */
@RestController
@RequestMapping("/api/customer")
@Tag(name = "Client", description = "Fournit des endpoints pour consulter les produits, créer des commandes, uploader des images, récupérer les commandes d’un client et gérer le panier.")
public class CustomerController {

    private final CustomerService customerService;
    private final CartService cartService;
    private final ProductService productService;
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection des services Panier et Produits
     * @param cartService Service métier pour la gestion des opérations sur le panier
     * @param productService Service métier pour la gestion des opérations sur les produits
     * @param customerService Service métier pour la gestion des opérations clients
     */
    public CustomerController(CustomerService customerService, CartService cartService, ProductService productService, UserRepository userRepository) {
        this.customerService = customerService;
        this.cartService = cartService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/customer/products
     * Récupère la liste de tous les produits disponibles.
     *
     * @return ResponseEntity contenant la liste des produits (ProductResponse).
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/products/by-category")
    @Operation(summary = "Récupère les produits par catégorie")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

        /**
     * GET /api/customer/products/{id}
     * Récupère un produit spécifique par son identifiant.
     *
     * @param id Identifiant du produit.
     * @return Produit sous forme de ProductResponse.
     */
    @Operation(summary = "Récupère un produit spécifique par son identifiant.")
    @GetMapping("/products/{id}")
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
    public ResponseEntity<CartResponse> addToCart(@RequestParam Long productId, @RequestParam int quantity, boolean isCustomized) {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity,isCustomized));
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
    public ResponseEntity<List<OrderResponse>> getCustomerOrders() {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long customerId = user.getId();
        return ResponseEntity.ok(customerService.getOrdersByCustomer(customerId));
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

    /**
     * DELETE /api/customer/cart/clear
     * Vide complètement le panier d’un client authentifié.
     *
     * @return ResponseEntity contenant le panier vide (CartResponse).
     */
    @Operation(summary = "Vider le panier", description = "Supprime tous les articles du panier de l'utilisateur authentifié.")

    @DeleteMapping("/cart/clear")
    public ResponseEntity<CartResponse> clearCart() {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long userId = user.getId();
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}
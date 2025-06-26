package com.customworld.service.impl;

import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.CustomOrder;
import com.customworld.entity.Delivery;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.enums.DeliveryStatus;
import com.customworld.enums.OrderStatus;
import com.customworld.enums.UserRole;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.CustomOrderRepository;
import com.customworld.repository.DeliveryRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.UserRepository;
import com.customworld.service.AdminService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service d'implémentation pour les opérations administratives.
 * Gère les utilisateurs, commandes, produits et livraisons.
 */

@Service
@Transactional
public  class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CustomOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;
    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);


    public AdminServiceImpl(UserRepository userRepository,
                            CustomOrderRepository orderRepository,
                            ProductRepository productRepository,
                            DeliveryRepository deliveryRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.deliveryRepository = deliveryRepository;
    }
    /**
     * Récupère tous les utilisateurs.
     *
     * @return Liste de tous les utilisateurs
     */


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Crée un nouvel utilisateur.
     *
     * @param user Entité utilisateur à créer
     * @return Utilisateur créé
     */
    @Override
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        log.info("Admin created user: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Récupère toutes les commandes.
     *
     * @return Liste des OrderResponse
     */
    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Assigne une commande à un livreur.
     *
     * @param orderId ID de la commande
     * @param delivererId ID du livreur
     * @return OrderResponse mise à jour
     * @throws ResourceNotFoundException Si la commande ou le livreur n'est pas trouvé
     */
    @Override
    public OrderResponse assignOrderToDeliverer(Long orderId, Long delivererId) {
        CustomOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for assignment: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée");
                });

        User deliverer = userRepository.findById(delivererId)
                .filter(user -> user.getRole().equals(UserRole.DELIVERY))
                .orElseThrow(() -> {
                    log.error("Deliverer not found or invalid role: {}", delivererId);
                    return new ResourceNotFoundException("Livreur non trouvé ou rôle invalide");
                });

        // Mise à jour du statut de la commande
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(order);
        log.info("Order {} status updated to PROCESSING", orderId);

        // Création de la livraison
        Delivery delivery = Delivery.builder()
                .order(order)
                .deliverer(deliverer)
                .status(DeliveryStatus.ASSIGNED)
                .build();

        deliveryRepository.save(delivery);
        log.info("Delivery created for order {} with deliverer {}", orderId, delivererId);

        return convertToOrderResponse(order);
    }

    /**
     * Valide un produit (marque comme approuvé par l'admin).
     *
     * @param productId ID du produit à valider
     * @return ProductResponse validé
     * @throws ResourceNotFoundException Si le produit n'est pas trouvé
     */
    @Override
    public ProductResponse validateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found for validation: {}", productId);
                    return new ResourceNotFoundException("Produit non trouvé");
                });

        product.setApproved(true);
        product = productRepository.save(product);
        log.info("Product {} validated by admin", productId);

        return convertToProductResponse(product);
    }

    /**
     * Convertit une commande en DTO OrderResponse.
     *
     * @param order Commande à convertir
     * @return OrderResponse DTO converti
     */
    private OrderResponse convertToOrderResponse(CustomOrder order) {
        // Récupère le premier produit de la commande (structure actuelle: 1 produit/commande)
        Product firstProduct = order.getItems().isEmpty() ? null : order.getItems().get(0).getProduct();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .productId(firstProduct != null ? firstProduct.getId() : null)
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .build();
    }

    /**
     * Convertit un produit en DTO ProductResponse.
     *
     * @param product Produit à convertir
     * @return ProductResponse DTO converti
     */
    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory().getName())
                .vendorId(product.getVendor().getId())
                .imagePath(product.getImagePath())
                .approved(product.isApproved()) // Nouveau champ
                .build();
    }

    @Override
    public <UserResponse> List<UserResponse> getUsersByRole(String role) {
        // TODO: Implémenter la logique réelle ici
        // Par exemple, récupérer les utilisateurs par rôle et convertir en UserResponse
        return null; // ou une liste vide temporairement
    }

    @Override
    public Object getDashboardStatistics(){

        // TODO: Implémenter la logique réelle ici
        return null;
    }

    @Override
    public   void assignDeliveryPerson(Long orderId, Long deliveryPersonId){}
}
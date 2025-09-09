package com.customworld.service.impl;

import com.customworld.dto.request.OrderCreationRequest;
import com.customworld.dto.response.OrderItemResponse;
import com.customworld.dto.response.OrderResponse;
import com.customworld.entity.Cart;
import com.customworld.entity.CartItem;
import com.customworld.entity.CustomOrder;
import com.customworld.entity.OrderItem;
import com.customworld.entity.User;
import com.customworld.entity.Delivery;
import com.customworld.enums.DeliveryStatus;
import com.customworld.enums.OrderStatus;
import com.customworld.enums.UserRole;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.CartRepository;
import com.customworld.repository.CustomOrderRepository;
import com.customworld.repository.OrderItemRepository;
import com.customworld.repository.UserRepository;
import com.customworld.repository.DeliveryRepository;
import com.customworld.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service d'implémentation pour la gestion des commandes.
 * Gère la création, la récupération, la mise à jour et l'annulation des commandes.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final CustomOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(CustomOrderRepository orderRepository,
                            UserRepository userRepository,
                            CartRepository cartRepository,
                            OrderItemRepository orderItemRepository,
                            DeliveryRepository deliveryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Crée une commande à partir du panier de l'utilisateur.
     *
     * @param customerId ID du client
     * @param orderRequest Requête contenant les informations de livraison
     * @return OrderResponse contenant les détails de la commande créée
     */
    @Override
    public OrderResponse createOrderFromCart(Long customerId, OrderCreationRequest orderRequest) {
        // Validate customer
        if (customerId == null || customerId <= 0) {
            log.error("Invalid customer ID: {}", customerId);
            throw new IllegalArgumentException("ID du client invalide");
        }
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found: {}", customerId);
                    return new ResourceNotFoundException("Client non trouvé");
                });

        // Validate order request
        if (orderRequest == null || orderRequest.getDeliveryAddress() == null || orderRequest.getDeliveryAddress().trim().isEmpty()) {
            log.error("Invalid delivery address for customer: {}", customerId);
            throw new IllegalArgumentException("Adresse de livraison requise");
        }
        if (orderRequest.getModeLivraison() == null || orderRequest.getModeLivraison() < 0) {
            log.error("Invalid delivery mode for customer: {}", customerId);
            throw new IllegalArgumentException("Mode de livraison invalide");
        }
        if (orderRequest.getPhone() == null || orderRequest.getPhone().trim().isEmpty()) {
            log.error("Invalid phone number for customer: {}", customerId);
            throw new IllegalArgumentException("Numéro de téléphone requis");
        }

        // Fetch and validate cart
        Cart cart = cartRepository.findByUserId(customerId)
                .orElseThrow(() -> {
                    log.error("Cart not found for user: {}", customerId);
                    return new ResourceNotFoundException("Panier non trouvé");
                });

        List<CartItem> cartItems = cart.getItems();
        if (cartItems == null || cartItems.isEmpty()) {
            log.error("Cart is empty for user: {}", customerId);
            throw new IllegalStateException("Le panier est vide");
        }

        // Calculate total amount
        Double totalAmount = cartItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
                .map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum);

        if (totalAmount <= 0) {
            log.error("Invalid total amount for order: {}", totalAmount);
            throw new IllegalStateException("Le montant total de la commande est invalide");
        }

        // Create order
        CustomOrder order = CustomOrder.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .modeLivraison(orderRequest.getModeLivraison())
                .phone(orderRequest.getPhone())
                .amount(totalAmount)
                .currency("USD") // Default currency
                .transactionId("txn_" + System.currentTimeMillis()) // Generated transactionId
                .items(new ArrayList<>())
                .build();

        try {
            order = orderRepository.save(order);
            log.info("Order created: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to save order for customer {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Erreur lors de l'enregistrement de la commande", e);
        }

        // Create OrderItems from CartItems
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct() == null) {
                log.warn("Skipping cart item with null product for order: {}", order.getId());
                continue;
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .imagePath(cartItem.getProduct().getImagePath())
                    .isCustomized(cartItem.isCustomized())
                    .build();
            try {
                order.getItems().add(orderItem);
                orderItemRepository.save(orderItem);
                log.info("Order item added: product {} to order: {}", cartItem.getProduct().getId(), order.getId());
            } catch (Exception e) {
                log.error("Failed to save order item for product {} in order {}: {}", 
                          cartItem.getProduct().getId(), order.getId(), e.getMessage());
                throw new RuntimeException("Erreur lors de l'enregistrement de l'article de commande", e);
            }
        }

        // Verify order items
        if (order.getItems().isEmpty()) {
            try {
                orderRepository.delete(order); // Rollback order if no valid items
                log.error("No valid items added to order: {}", order.getId());
                throw new IllegalStateException("Aucun article valide dans la commande");
            } catch (Exception e) {
                log.error("Failed to rollback order {}: {}", order.getId(), e.getMessage());
                throw new RuntimeException("Erreur lors de l'annulation de la commande", e);
            }
        }

        // Clear the cart
        try {
            cart.getItems().clear();
            cartRepository.save(cart);
            log.info("Cart cleared for user: {}", customerId);
        } catch (Exception e) {
            log.error("Failed to clear cart for user {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du panier", e);
        }

        return convertToOrderResponse(order);
    }

    /**
     * Récupère une commande spécifique par son ID.
     *
     * @param orderId ID de la commande
     * @return OrderResponse contenant les détails de la commande
     */
    @Override
    public OrderResponse getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            log.error("Invalid order ID: {}", orderId);
            throw new ResourceNotFoundException("ID de commande invalide");
        }

        CustomOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée pour l'ID: " + orderId);
                });

        List<OrderItem> items = order.getItems();
        if (items == null || items.isEmpty()) {
            log.warn("Order has no items: {}", orderId);
            throw new IllegalStateException("La commande n'a pas d'articles");
        }

        return convertToOrderResponse(order);
    }

    /**
     * Récupère toutes les commandes d'un client spécifique.
     *
     * @param customerId ID du client
     * @return Liste des OrderResponse
     */
    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        if (customerId == null || customerId <= 0) {
            log.error("Invalid customer ID: {}", customerId);
            throw new IllegalArgumentException("ID du client invalide");
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les commandes (pour admin).
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
     * Met à jour le statut d'une commande.
     *
     * @param orderId ID de la commande
     * @param status Nouveau statut
     * @return OrderResponse mise à jour
     */
    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        if (orderId == null || orderId <= 0) {
            log.error("Invalid order ID for status update: {}", orderId);
            throw new IllegalArgumentException("ID de commande invalide");
        }
        if (status == null) {
            log.error("Invalid status for order: {}", orderId);
            throw new IllegalArgumentException("Statut de commande invalide");
        }
        CustomOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for status update: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée");
                });
        order.setStatus(status);
        try {
            order = orderRepository.save(order);
            log.info("Order {} status updated to {}", orderId, status);
        } catch (Exception e) {
            log.error("Failed to update order status for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la commande", e);
        }
        return convertToOrderResponse(order);
    }

    /**
     * Assigne une commande à un livreur.
     *
     * @param orderId ID de la commande
     * @param delivererId ID du livreur
     * @return OrderResponse mise à jour
     */
    @Override
    public OrderResponse assignOrderToDeliverer(Long orderId, Long delivererId) {
        if (orderId == null || orderId <= 0) {
            log.error("Invalid order ID for assignment: {}", orderId);
            throw new IllegalArgumentException("ID de commande invalide");
        }
        if (delivererId == null || delivererId <= 0) {
            log.error("Invalid deliverer ID for order: {}", orderId);
            throw new IllegalArgumentException("ID du livreur invalide");
        }
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

        order.setStatus(OrderStatus.IN_PROGRESS);
        try {
            orderRepository.save(order);
            log.info("Order {} status updated to IN_PROGRESS", orderId);
        } catch (Exception e) {
            log.error("Failed to update order status for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la commande", e);
        }

        Delivery delivery = Delivery.builder()
                .order(order)
                .deliverer(deliverer)
                .status(DeliveryStatus.ASSIGNED)
                .build();

        try {
            deliveryRepository.save(delivery);
            log.info("Delivery created for order {} with deliverer {}", orderId, delivererId);
        } catch (Exception e) {
            log.error("Failed to save delivery for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Erreur lors de l'enregistrement de la livraison", e);
        }

        return convertToOrderResponse(order);
    }

    /**
     * Annule une commande.
     *
     * @param orderId ID de la commande
     */
    @Override
    public void cancelOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            log.error("Invalid order ID for cancellation: {}", orderId);
            throw new IllegalArgumentException("ID de commande invalide");
        }
        CustomOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for cancellation: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée");
                });

        if (order.getStatus().canBeCancelled()) {
            order.setStatus(OrderStatus.CANCELLED);
            try {
                orderRepository.save(order);
                log.info("Order cancelled: {}", orderId);
            } catch (Exception e) {
                log.error("Failed to cancel order {}: {}", orderId, e.getMessage());
                throw new RuntimeException("Erreur lors de l'annulation de la commande", e);
            }
        } else {
            log.warn("Attempt to cancel order in non-cancellable status: {}", order.getStatus());
            throw new IllegalStateException("La commande ne peut pas être annulée dans son état actuel");
        }
    }

    /**
     * Convertit une commande en DTO OrderResponse.
     *
     * @param order Commande à convertir
     * @return OrderResponse DTO converti
     */
    private OrderResponse convertToOrderResponse(CustomOrder order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                        .imagePath(item.getImagePath())
                        .isCustomized(item.isCustomized())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .transactionId(order.getTransactionId())
                .modeLivraison(order.getModeLivraison())
                .phone(order.getPhone())
                .items(itemResponses)
                .build();
    }
}

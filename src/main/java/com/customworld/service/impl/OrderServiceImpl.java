package com.customworld.service.impl;

import com.customworld.dto.request.CustomOrderRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.entity.CustomOrder;
import com.customworld.entity.OrderItem;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.entity.Delivery;
import com.customworld.enums.DeliveryStatus;
import com.customworld.enums.OrderStatus;
import com.customworld.enums.UserRole;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.CustomOrderRepository;
import com.customworld.repository.OrderItemRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.UserRepository;
import com.customworld.repository.DeliveryRepository;
import com.customworld.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(CustomOrderRepository orderRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            OrderItemRepository orderItemRepository,
                            DeliveryRepository deliveryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Crée une nouvelle commande à partir d'une requête.
     *
     * @param orderRequest Requête de création de commande
     * @return OrderResponse contenant les détails de la commande créée
     */
    @Override
    public OrderResponse createOrder(CustomOrderRequest orderRequest) {
        User customer = userRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> {
                    log.error("Customer not found: {}", orderRequest.getCustomerId());
                    return new ResourceNotFoundException("Client non trouvé");
                });

        Product product = productRepository.findById(orderRequest.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found: {}", orderRequest.getProductId());
                    return new ResourceNotFoundException("Produit non trouvé");
                });

        CustomOrder order = CustomOrder.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .modeLivraison(orderRequest.getModeLivraison())
                .phone(orderRequest.getPhone())
                .amount(product.getPrice()) // Use product price as amount
                .currency("XAF") // Default currency
                .transactionId("txn_" + System.currentTimeMillis()) // Generate transactionId
                .build();

        order = orderRepository.save(order);
        log.info("Order created: {}", order.getId());

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .imagePath(orderRequest.getImagePath())
                .isCustomized(false) // Default to false, adjust if needed
                .build();

        orderItemRepository.save(orderItem);
        log.info("Order item added: {} to order: {}", product.getId(), order.getId());

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
        CustomOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for status update: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée");
                });
        order.setStatus(status);
        order = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, status);
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
        orderRepository.save(order);
        log.info("Order {} status updated to IN_PROGRESS", orderId);

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
     * Annule une commande.
     *
     * @param orderId ID de la commande
     */
    @Override
    public void cancelOrder(Long orderId) {
        CustomOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for cancellation: {}", orderId);
                    return new ResourceNotFoundException("Commande non trouvée");
                });

        if (order.getStatus().canBeCancelled()) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Order cancelled: {}", orderId);
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
        List<OrderItem> items = order.getItems();
        Long productId = items != null && !items.isEmpty() ? items.get(0).getProduct().getId() : null;

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .productId(productId)
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .transactionId(order.getTransactionId())
                .modeLivraison(order.getModeLivraison())
                .phone(order.getPhone())
                .build();
    }
}
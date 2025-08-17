package com.customworld.service.impl;

import com.customworld.dto.request.CustomOrderRequest;
import com.customworld.dto.response.OrderResponse;
import com.customworld.dto.response.ProductResponse;
import com.customworld.entity.CustomOrder;
import com.customworld.entity.OrderItem;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.enums.OrderStatus;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.*;
import com.customworld.service.CustomerService;
import com.customworld.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service d'implémentation pour les opérations liées aux clients.
 * Gère la création de commandes, la récupération de produits et la gestion des commandes clients.
 */

@Service
@Transactional
public  class CustomerServiceImpl implements CustomerService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CustomOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FileStorageService fileStorageService;
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(UserRepository userRepository,
                            CustomOrderRepository orderRepository,
                            ProductRepository productRepository,
                               OrderItemRepository orderItemRepository,
                               FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.fileStorageService = fileStorageService;
    }
    

    /**
     * Crée une nouvelle commande pour un client.
     *
     * @param orderRequest DTO contenant les informations de la commande
     * @return OrderResponse représentant la commande créée
     * @throws ResourceNotFoundException Si le client ou le produit n'est pas trouvé
     */
    @Override
    public OrderResponse createOrder(CustomOrderRequest orderRequest) {
        // Validation des entités existantes
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

        // Création de la commande
        CustomOrder order = CustomOrder.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .build();

        order = orderRepository.save(order);
        log.info("Order created: {}", order.getId());

        // Création de l'article de commande
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .imagePath(orderRequest.getImagePath())
                .build();

        orderItemRepository.save(orderItem);
        log.info("Order item added: {} to order: {}", product.getId(), order.getId());

        return convertToOrderResponse(order, product);
    }

    /**
     * Téléverse une image pour un produit.
     *
     * @param file Fichier image à téléverser
     * @return Chemin d'accès à l'image téléversée
     */
    @Override
    public String uploadImage(MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        log.info("Image uploaded: {}", fileName);
        return fileName;
    }

    /**
     * Récupère les commandes d'un client spécifique.
     *
     * @param customerId ID du client
     * @return Liste des OrderResponse pour ce client
     */
    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> {
                    // On suppose qu'une commande a au moins un article
                    Product product = order.getItems().get(0).getProduct();
                    return convertToOrderResponse(order, product);
                })
                .collect(Collectors.toList());
    }

    /**
     * Annule une commande existante.
     *
     * @param orderId ID de la commande à annuler
     * @throws ResourceNotFoundException Si la commande n'est pas trouvée
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
     * Convertit une entité Product en DTO ProductResponse.
     *
     * @param product Entité Product à convertir
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
                .build();
    }

    /**
     * Convertit une entité CustomOrder en DTO OrderResponse.
     *
     * @param order Commande à convertir
     * @param product Produit associé à la commande
     * @return OrderResponse DTO converti
     */
    private OrderResponse convertToOrderResponse(CustomOrder order, Product product) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .productId(product.getId())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .build();
    }
}
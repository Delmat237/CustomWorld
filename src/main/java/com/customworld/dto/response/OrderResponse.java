package com.customworld.dto.response;

import com.customworld.enums.OrderStatus;

import java.time.LocalDateTime;

/**
 * DTO représentant la réponse d'une commande.
 * Contient les informations principales d'une commande pour la communication avec le client.
 */
public class OrderResponse {

    private Long id;
    private Long customerId;
    private Long productId;
    private String deliveryAddress;
    private OrderStatus status;
    private LocalDateTime orderDate;

    /**
     * Constructeur sans argument.
     */
    public OrderResponse() {
    }

    /**
     * Constructeur complet.
     */
    public OrderResponse(Long id, Long customerId, Long productId, String deliveryAddress, OrderStatus status, LocalDateTime orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.orderDate = orderDate;
    }

    // Getters et Setters explicites

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Builder statique pour faciliter la création d'instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long customerId;
        private Long productId;
        private String deliveryAddress;
        private OrderStatus status;
        private LocalDateTime orderDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder deliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public OrderResponse build() {
            return new OrderResponse(id, customerId, productId, deliveryAddress, status, orderDate);
        }
    }
}

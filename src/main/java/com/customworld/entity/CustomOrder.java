package com.customworld.entity;

import com.customworld.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Représente une commande client dans le système.
 * Contient toutes les informations relatives à une transaction commerciale.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "custom_orders")
public class CustomOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Client associé à la commande.
     * Relation many-to-one: plusieurs commandes peuvent appartenir à un client.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    /**
     * Liste des articles dans la commande.
     * Cascade : les opérations sur la commande affectent ses articles.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    /**
     * État actuel de la commande (ex: CREATED, PAID, SHIPPED)
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * Date/heure de création de la commande.
     * Valeur par défaut: timestamp de création
     */
    private LocalDateTime orderDate;

    /**
     * Adresse de livraison complète
     */
    private String deliveryAddress;

    // Getters et Setters explicites

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    // Builder manuel

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User customer;
        private List<OrderItem> items;
        private OrderStatus status;
        private LocalDateTime orderDate;
        private String deliveryAddress;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder customer(User customer) {
            this.customer = customer;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
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

        public Builder deliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public CustomOrder build() {
            CustomOrder order = new CustomOrder();
            order.setId(id);
            order.setCustomer(customer);
            order.setItems(items);
            order.setStatus(status);
            order.setOrderDate(orderDate);
            order.setDeliveryAddress(deliveryAddress);
            return order;
        }
    }
}

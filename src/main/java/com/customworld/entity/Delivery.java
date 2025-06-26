package com.customworld.entity;

import com.customworld.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Représente une livraison associée à une commande.
 * Gère le processus d'expédition et de suivi.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Commande associée à cette livraison.
     * Relation many-to-one: une livraison par commande.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private CustomOrder order;

    /**
     * Livreur assigné pour la livraison.
     * Doit avoir le rôle DELIVERER dans le système.
     */
    @ManyToOne
    @JoinColumn(name = "deliverer_id")
    private User deliverer;

    /**
     * État actuel de la livraison (ex: PENDING, IN_TRANSIT, DELIVERED)
     */
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(length = 500)
    private String issueDescription;

    private LocalDateTime deliveryDate;

    // Getters et Setters explicites

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomOrder getOrder() {
        return order;
    }

    public void setOrder(CustomOrder order) {
        this.order = order;
    }

    public User getDeliverer() {
        return deliverer;
    }

    public void setDeliverer(User deliverer) {
        this.deliverer = deliverer;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    // Builder manuel

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private CustomOrder order;
        private User deliverer;
        private DeliveryStatus status;
        private String issueDescription;
        private LocalDateTime deliveryDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder order(CustomOrder order) {
            this.order = order;
            return this;
        }

        public Builder deliverer(User deliverer) {
            this.deliverer = deliverer;
            return this;
        }

        public Builder status(DeliveryStatus status) {
            this.status = status;
            return this;
        }

        public Builder issueDescription(String issueDescription) {
            this.issueDescription = issueDescription;
            return this;
        }

        public Builder deliveryDate(LocalDateTime deliveryDate) {
            this.deliveryDate = deliveryDate;
            return this;
        }

        public Delivery build() {
            Delivery delivery = new Delivery();
            delivery.setId(id);
            delivery.setOrder(order);
            delivery.setDeliverer(deliverer);
            delivery.setStatus(status);
            delivery.setIssueDescription(issueDescription);
            delivery.setDeliveryDate(deliveryDate);
            return delivery;
        }
    }
}

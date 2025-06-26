package com.customworld.dto.response;

import com.customworld.enums.DeliveryStatus;

import java.time.LocalDateTime;

/**
 * DTO représentant la réponse d'une livraison.
 * Contient les informations principales d'une livraison pour la communication avec le client.
 */
public class DeliveryResponse {

    private Long id;
    private Long orderId;
    private Long delivererId;
    private DeliveryStatus status;
    private String issueDescription;
    private LocalDateTime deliveryDate;

    /**
     * Constructeur sans argument.
     */
    public DeliveryResponse() {
    }

    /**
     * Constructeur complet.
     */
    public DeliveryResponse(Long id, Long orderId, Long delivererId, DeliveryStatus status,
                            String issueDescription, LocalDateTime deliveryDate) {
        this.id = id;
        this.orderId = orderId;
        this.delivererId = delivererId;
        this.status = status;
        this.issueDescription = issueDescription;
        this.deliveryDate = deliveryDate;
    }

    // Getters et Setters explicites

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDelivererId() {
        return delivererId;
    }

    public void setDelivererId(Long delivererId) {
        this.delivererId = delivererId;
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

    /**
     * Builder statique pour faciliter la création d'instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long orderId;
        private Long delivererId;
        private DeliveryStatus status;
        private String issueDescription;
        private LocalDateTime deliveryDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder delivererId(Long delivererId) {
            this.delivererId = delivererId;
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

        public DeliveryResponse build() {
            return new DeliveryResponse(id, orderId, delivererId, status, issueDescription, deliveryDate);
        }
    }
}

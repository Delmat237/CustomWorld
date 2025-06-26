package com.customworld.dto.request;

/**
 * DTO représentant une requête de création de commande personnalisée.
 * Contient les informations nécessaires pour créer une commande client.
 */
public class CustomOrderRequest {

    private Long customerId;
    private Long productId;
    private String deliveryAddress;
    private String imagePath;

    /**
     * Constructeur sans argument.
     */
    public CustomOrderRequest() {
    }

    // Getters et Setters explicites

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

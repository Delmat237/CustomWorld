package com.customworld.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO représentant une requête de création ou mise à jour d'un produit.
 * Contient les informations nécessaires pour gérer un produit.
 */
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Double price;

    @NotBlank
    private String category;

    @NotNull
    private Long vendorId;

    private String imagePath;

    /**
     * Constructeur sans argument.
     */
    public ProductRequest() {
    }

    // Getters et Setters explicites

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un produit commercialisé sur la plateforme.
 * Peut être associé à un vendeur et une catégorie.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom public du produit
     */
    private String name;

    /**
     * Description détaillée du produit
     */
    private String description;

    /**
     * Prix actuel du produit.
     * Utiliser BigDecimal en production pour la précision monétaire.
     */
    private Double price;

    /**
     * Catégorie principale du produit
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Vendeur/propriétaire du produit.
     * Doit avoir le rôle VENDOR dans le système.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private User vendor;

    /**
     * Chemin d'accès à l'image principale du produit
     */
    private String imagePath;

    /**
     * Indique si le produit est approuvé pour publication.
     * Par défaut, false.
     */
    private boolean approved = false;

    // Getters et Setters explicites

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getVendor() {
        return vendor;
    }

    public void setVendor(User vendor) {
        this.vendor = vendor;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    // Builder manuel

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Category category;
        private User vendor;
        private String imagePath;
        private boolean approved;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder vendor(User vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder approved(boolean approved) {
            this.approved = approved;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setVendor(vendor);
            product.setImagePath(imagePath);
            product.setApproved(approved);
            return product;
        }
    }
}

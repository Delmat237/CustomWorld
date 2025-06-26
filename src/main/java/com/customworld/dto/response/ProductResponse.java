package com.customworld.dto.response;

/**
 * DTO représentant la réponse d'un produit.
 * Contient les informations principales d'un produit pour la communication avec le client.
 */
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Long vendorId;
    private String imagePath;
    private boolean approved;

    /**
     * Constructeur sans argument.
     */
    public ProductResponse() {
    }

    /**
     * Constructeur complet.
     */
    public ProductResponse(Long id, String name, String description, Double price, String category,
                           Long vendorId, String imagePath, boolean approved) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.vendorId = vendorId;
        this.imagePath = imagePath;
        this.approved = approved;
    }

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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * Builder statique pour faciliter la création d'instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private String category;
        private Long vendorId;
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

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder vendorId(Long vendorId) {
            this.vendorId = vendorId;
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

        public ProductResponse build() {
            return new ProductResponse(id, name, description, price, category, vendorId, imagePath, approved);
        }
    }
}

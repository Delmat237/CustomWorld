package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un article individuel dans une commande.
 * Fait le lien entre les produits et les commandes.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Commande parente contenant cet article.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private CustomOrder order;

    /**
     * Produit spécifique commandé.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Chemin d'accès à l'image du produit au moment de la commande.
     * Permet de conserver une trace historique des images.
     */
    private String imagePath;

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Builder manuel

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private CustomOrder order;
        private Product product;
        private String imagePath;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder order(CustomOrder order) {
            this.order = order;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public OrderItem build() {
            OrderItem item = new OrderItem();
            item.setId(id);
            item.setOrder(order);
            item.setProduct(product);
            item.setImagePath(imagePath);
            return item;
        }
    }
}

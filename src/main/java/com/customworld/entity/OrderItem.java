package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un article individuel dans une commande.
 * Fait le lien entre les produits et les commandes.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Builder.Default
    private boolean isCustomized=false;


}

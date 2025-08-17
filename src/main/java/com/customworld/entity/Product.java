package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
/**
 * Représente un produit commercialisé sur la plateforme.
 * Peut être associé à un vendeur et une catégorie.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
     * Prix final du produit. avec design
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
    @Builder.Default
    private boolean approved = false;

    /*
     * Couleur
     *  
     */
     private List<String> color;

     /*
      * originalPrice
      */

     private Double originalPrice;
    /*  
     * isnew
     */
    @Builder.Default
    private boolean isNew = true;

     /*
      * rating
      */
     private Integer rating;
      /*
       * reviews
       */
      private Integer reviews;
}

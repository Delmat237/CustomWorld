package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente une catégorie de produits dans le système.
 * Les catégories permettent d'organiser hiérarchiquement les produits.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom unique de la catégorie (ex: "Électronique", "Vêtements")
     * Champ obligatoire et indexable en production
     */
    @Column(nullable = false)
    private String name;

    private String description;
}

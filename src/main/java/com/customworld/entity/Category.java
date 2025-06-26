package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente une catégorie de produits dans le système.
 * Les catégories permettent d'organiser hiérarchiquement les produits.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}

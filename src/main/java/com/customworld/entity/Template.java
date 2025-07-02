package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String templatePath; // Chemin vers le fichier du template (si applicable)

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy; // Utilisateur qui a créé le template
}
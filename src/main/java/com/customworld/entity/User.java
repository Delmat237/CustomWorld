package com.customworld.entity;

import com.customworld.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;

/**
 * Représente un utilisateur du système (client, vendeur, livreur, admin).
 * Centralise les informations d'authentification et de profil.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email unique utilisé comme identifiant de connexion.
     * Validation basique - une validation supplémentaire est recommandée.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Mot de passe hashé (doit utiliser BCrypt ou équivalent).
     * Jamais stocké en clair.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rôle principal déterminant les permissions.
     * Valeurs possibles : CUSTOMER, VENDOR, DELIVERER, ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Nom complet de l'utilisateur
     */
    private String name;

    /**
     * Adresse physique principale
     */
    private String address;
    /**
     * Numro de telephone
     */
    private String phone;

    /**
     * Champ qui permettront de reset le mot de passe en cas d'oublie
     */
    private String passwordResetToken;
    private Instant passwordResetTokenExpiry;

        @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

}

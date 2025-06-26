package com.customworld.entity;

import com.customworld.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un utilisateur du système (client, vendeur, livreur, admin).
 * Centralise les informations d'authentification et de profil.
 */
@Entity
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


    // Getters et Setters explicites

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

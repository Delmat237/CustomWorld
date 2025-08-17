package com.customworld.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

/**
 * Entité représentant un refresh token JWT pour l'authentification.
 * Un refresh token est associé à un utilisateur et utilisé pour générer de nouveaux access tokens.
 */
@Entity
@Data
@Table(name = "refresh_tokens")
public class RefreshToken {

    /**
     * Identifiant unique du refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Utilisateur associé au refresh token.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * Valeur du refresh token (JWT signé).
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Date d'expiration du refresh token.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}
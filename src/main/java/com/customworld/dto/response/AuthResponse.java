package com.customworld.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.customworld.entity.User;

/**
 * DTO représentant la réponse d'authentification pour les opérations de connexion et de rafraîchissement de token.
 * Contient l'access token JWT, le refresh token, et le rôle de l'utilisateur.
 */
@Data
@NoArgsConstructor
@Builder
public class AuthResponse {

    /**
     * Access token JWT utilisé pour authentifier les requêtes API.
     */
    private String accessToken;

    /**
     * Refresh token JWT utilisé pour générer un nouvel access token.
     */
    private String refreshToken;

  
    /*
     * Information de l'utilisateur
     */
    private User user;
    /**
     * Constructeur complet pour initialiser tous les champs.
     *
     * @param accessToken Access token JWT.
     * @param refreshToken Refresh token JWT.
     * @param user
     */
    public AuthResponse(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
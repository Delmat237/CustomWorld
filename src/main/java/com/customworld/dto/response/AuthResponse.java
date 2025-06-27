package com.customworld.dto.response;

import lombok.*;

/**
 * DTO représentant la réponse d'authentification.
 * Contient le token JWT et le rôle de l'utilisateur.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String role;


}

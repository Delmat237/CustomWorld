package com.customworld.dto.response;

/**
 * DTO représentant la réponse d'authentification.
 * Contient le token JWT et le rôle de l'utilisateur.
 */
public class AuthResponse {

    private String token;
    private String role;

    /**
     * Constructeur sans argument.
     */
    public AuthResponse() {
    }

    /**
     * Constructeur complet.
     *
     * @param token Token JWT d'authentification.
     * @param role Rôle de l'utilisateur.
     */
    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getters et Setters explicites

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Builder statique pour faciliter la création d'instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String role;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, role);
        }
    }
}

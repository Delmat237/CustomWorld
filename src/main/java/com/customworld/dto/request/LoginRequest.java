package com.customworld.dto.request;

/**
 * DTO représentant une requête de connexion.
 * Contient les informations nécessaires pour authentifier un utilisateur.
 */
public class LoginRequest {

    private String email;
    private String password;

    /**
     * Constructeur sans argument.
     */
    public LoginRequest() {
    }

    // Getters et Setters explicites

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
}

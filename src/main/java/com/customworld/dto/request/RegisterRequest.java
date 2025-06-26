package com.customworld.dto.request;

import com.customworld.enums.UserRole;

/**
 * DTO pour la requête d'inscription d'un utilisateur.
 * Contient les informations nécessaires à la création d'un compte.
 */
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String address;
    private UserRole role;

    /**
     * Constructeur sans argument.
     */
    public RegisterRequest() {
    }

    // Getters et Setters explicites

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

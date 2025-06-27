package com.customworld.dto.request;

import com.customworld.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête d'inscription d'un utilisateur.
 * Contient les informations nécessaires à la création d'un compte.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String address;
    private UserRole role;

}

package com.customworld.dto.response;

import com.customworld.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO représentant la réponse d'un Usr.
 * Contient les informations principales d'un utilisateur.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private String name;
    private String address;
    private String phone;
    private String passwordResetToken;

}


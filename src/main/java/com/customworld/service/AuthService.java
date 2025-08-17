package com.customworld.service;

import com.customworld.dto.request.LoginRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.AuthResponse;

/**
 * Interface pour la gestion de l'authentification des utilisateurs.
 */
public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    AuthResponse refreshToken(String refreshToken);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
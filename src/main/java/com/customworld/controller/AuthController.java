package com.customworld.controller;

import com.customworld.dto.request.LoginRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.ApiResponse;
import com.customworld.dto.response.AuthResponse;
import com.customworld.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour la gestion de l'authentification des utilisateurs.
 * Fournit des endpoints pour la connexion et l'inscription.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructeur avec injection du service d'authentification.
     * @param authService Service métier pour la gestion de l'authentification.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Authentifie un utilisateur à partir de ses identifiants.
     *
     * @param loginRequest Objet contenant le nom d'utilisateur et le mot de passe.
     * @return ResponseEntity contenant la réponse d'authentification (token, etc.).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * POST /api/auth/register
     * Inscrit un nouvel utilisateur à partir des informations fournies.
     *
     * @param registerRequest Objet contenant les informations d'inscription.
     * @return ResponseEntity contenant un message de succès.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Utilisateur créé avec succès"));
    }
}

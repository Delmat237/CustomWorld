package com.customworld.controller;

import com.customworld.dto.request.LoginRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.ApiResponseWrapper;
import com.customworld.dto.response.AuthResponse;
import com.customworld.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur REST pour la gestion de l'authentification des utilisateurs.
 * Fournit des endpoints pour la connexion, l'inscription, le rafraîchissement du token, et la réinitialisation du mot de passe.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "API pour la gestion de l'authentification et des sessions utilisateur")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructeur avec injection du service d'authentification.
     *
     * @param authService Service métier pour la gestion de l'authentification.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authentifie un utilisateur à partir de ses identifiants.
     *
     * @param loginRequest Objet contenant l'email et le mot de passe.
     * @return ResponseEntity contenant la réponse d'authentification (accessToken, refreshToken, role).
     */
    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur et retourne un access token et un refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "400", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Inscrit un nouvel utilisateur et envoie un email de bienvenue.
     *
     * @param registerRequest Objet contenant les informations d'inscription (nom, email, mot de passe, etc.).
     * @return ResponseEntity contenant un message de succès.
     */
    @Operation(summary = "Inscription d'un utilisateur", description = "Crée un nouveau compte utilisateur et envoie un email de bienvenue.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseWrapper> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new ApiResponseWrapper(true, "Utilisateur créé avec succès"));
    }

    /**
     * Rafraîchit l'access token à partir d'un refresh token valide.
     *
     * @param refreshTokenHeader En-tête Authorization contenant le refresh token (Bearer <token>).
     * @return ResponseEntity contenant la nouvelle réponse d'authentification (accessToken, refreshToken, role).
     */
    @Operation(summary = "Rafraîchir le token", description = "Génère un nouvel access token et un nouveau refresh token à partir d'un refresh token valide.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès"),
            @ApiResponse(responseCode = "400", description = "Refresh token invalide ou expiré"),
            @ApiResponse(responseCode = "401", description = "En-tête Authorization manquant ou mal formé")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        String refreshToken = refreshTokenHeader.startsWith("Bearer ") ? refreshTokenHeader.substring(7) : refreshTokenHeader;
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Demande une réinitialisation du mot de passe pour un utilisateur.
     *
     * @param request Map contenant l'email de l'utilisateur.
     * @return ResponseEntity contenant un message de succès.
     */
    @Operation(summary = "Demander une réinitialisation de mot de passe", description = "Envoie un email avec un lien de réinitialisation à l'utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de réinitialisation envoyé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Email invalide")
    })
    @PostMapping("/reset-password-request")
    public ResponseEntity<ApiResponseWrapper> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(new ApiResponseWrapper(true, "Email de réinitialisation envoyé"));
    }

    /**
     * Réinitialise le mot de passe de l'utilisateur à partir d'un token de réinitialisation.
     *
     * @param request Map contenant le token de réinitialisation et le nouveau mot de passe.
     * @return ResponseEntity contenant un message de succès.
     */
    @Operation(summary = "Réinitialiser le mot de passe", description = "Met à jour le mot de passe de l'utilisateur à partir d'un token de réinitialisation valide.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseWrapper> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(new ApiResponseWrapper(true, "Mot de passe réinitialisé avec succès"));
    }
}
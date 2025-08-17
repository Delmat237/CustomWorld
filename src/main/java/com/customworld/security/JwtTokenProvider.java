package com.customworld.security;

import com.customworld.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Fournit des méthodes pour générer, valider et parser les tokens JWT (access tokens, refresh tokens, et tokens de réinitialisation de mot de passe).
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String ROLE_CLAIM = "role";
    private static final String RESET_TOKEN_TYPE = "reset";

    private final SecretKey secretKey;
    private final long jwtExpiration;
    private final long jwtRefreshExpiration;
    private final long jwtResetTokenExpiration;

    /**
     * Constructeur avec injection des propriétés de configuration JWT.
     *
     * @param jwtSecret Clé secrète pour signer les tokens.
     * @param jwtExpiration Durée d'expiration des access tokens (en millisecondes).
     * @param jwtRefreshExpiration Durée d'expiration des refresh tokens (en millisecondes).
     * @param jwtResetTokenExpiration Durée d'expiration des tokens de réinitialisation de mot de passe (en millisecondes).
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long jwtRefreshExpiration,
            @Value("${jwt.reset-token-expiration:1800000}") long jwtResetTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
        this.jwtResetTokenExpiration = jwtResetTokenExpiration;
    }

    /**
     * Génère un access token JWT pour un utilisateur.
     *
     * @param email Email de l'utilisateur.
     * @param role Rôle de l'utilisateur (CLIENT, VENDEUR, LIVREUR, ADMIN).
     * @return Access token JWT signé.
     * @throws JwtAuthenticationException si la génération du token échoue.
     */
    public String generateToken(String email, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        try {
            return Jwts.builder()
                    .setSubject(email)
                    .claim(ROLE_CLAIM, role.name())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (JwtException e) {
            log.error("Erreur lors de la génération de l'access token pour {}", email, e);
            throw new JwtAuthenticationException("Impossible de générer l'access token");
        }
    }

    /**
     * Génère un refresh token JWT pour un utilisateur.
     *
     * @param email Email de l'utilisateur.
     * @param role Rôle de l'utilisateur.
     * @return Refresh token JWT signé.
     * @throws JwtAuthenticationException si la génération du token échoue.
     */
    public String generateRefreshToken(String email, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpiration);
        try {
            return Jwts.builder()
                    .setSubject(email)
                    .claim(ROLE_CLAIM, role.name())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (JwtException e) {
            log.error("Erreur lors de la génération du refresh token pour {}", email, e);
            throw new JwtAuthenticationException("Impossible de générer le refresh token");
        }
    }

    /**
     * Génère un token de réinitialisation de mot de passe pour un utilisateur.
     *
     * @param email Email de l'utilisateur.
     * @return Token de réinitialisation signé.
     * @throws JwtAuthenticationException si la génération du token échoue.
     */
    public String generateResetToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtResetTokenExpiration);
        try {
            return Jwts.builder()
                    .setSubject(email)
                    .claim("type", RESET_TOKEN_TYPE)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (JwtException e) {
            log.error("Erreur lors de la génération du token de réinitialisation pour {}", email, e);
            throw new JwtAuthenticationException("Impossible de générer le token de réinitialisation");
        }
    }

    /**
     * Récupère l'email à partir d'un token JWT.
     *
     * @param token Token JWT (access, refresh ou reset).
     * @return Email de l'utilisateur.
     * @throws JwtAuthenticationException si le token est invalide ou expiré.
     */
    public String getEmailFromJWT(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * Récupère le rôle à partir d'un token JWT.
     *
     * @param token Token JWT (access ou refresh).
     * @return Rôle de l'utilisateur.
     * @throws JwtAuthenticationException si le token est invalide ou ne contient pas de rôle.
     */
    public UserRole getRoleFromJWT(String token) {
        Claims claims = parseToken(token);
        String role = claims.get(ROLE_CLAIM, String.class);
        if (role == null) {
            throw new JwtAuthenticationException("Rôle manquant dans le token");
        }
        return UserRole.valueOf(role);
    }

    /**
     * Valide un access ou refresh token JWT.
     *
     * @param token Token JWT à valider.
     * @return true si le token est valide, false sinon.
     * @throws JwtAuthenticationException si le token est invalide ou expiré.
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtAuthenticationException ex) {
            log.debug("Échec de la validation du token : {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Valide un token de réinitialisation de mot de passe.
     *
     * @param token Token de réinitialisation à valider.
     * @return true si le token est valide et de type reset, false sinon.
     * @throws JwtAuthenticationException si le token est invalide, expiré ou de mauvais type.
     */
    public boolean validateResetToken(String token) {
        try {
            Claims claims = parseToken(token);
            String type = claims.get("type", String.class);
            if (!RESET_TOKEN_TYPE.equals(type)) {
                throw new JwtAuthenticationException("Token de réinitialisation invalide");
            }
            return true;
        } catch (JwtAuthenticationException ex) {
            log.debug("Échec de la validation du token de réinitialisation : {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Parse un token JWT et retourne ses claims.
     *
     * @param token Token JWT à parser.
     * @return Claims du token.
     * @throws JwtAuthenticationException si le token est invalide, expiré, ou mal formé.
     */
    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expiré : {}", ex.getMessage());
            throw new JwtAuthenticationException("Token expiré", ex);
        } catch (UnsupportedJwtException ex) {
            log.warn("Token JWT non supporté : {}", ex.getMessage());
            throw new JwtAuthenticationException("Token non supporté", ex);
        } catch (MalformedJwtException ex) {
            log.warn("Token JWT mal formé : {}", ex.getMessage());
            throw new JwtAuthenticationException("Token invalide", ex);
        } catch (SignatureException ex) {
            log.warn("Échec de la vérification de la signature JWT : {}", ex.getMessage());
            throw new JwtAuthenticationException("Signature invalide", ex);
        } catch (IllegalArgumentException ex) {
            log.warn("Token JWT vide ou nul : {}", ex.getMessage());
            throw new JwtAuthenticationException("Token vide ou nul", ex);
        }
    }

    /**
     * Récupère la durée d'expiration des refresh tokens.
     *
     * @return Durée en millisecondes.
     */
    public long getJwtRefreshExpiration() {
        return jwtRefreshExpiration;
    }

    /**
     * Récupère la durée d'expiration des tokens de réinitialisation.
     *
     * @return Durée en millisecondes.
     */
    public long getJwtResetTokenExpiration() {
        return jwtResetTokenExpiration;
    }

    /**
     * Exception personnalisée pour les erreurs liées aux tokens JWT.
     */
    public static class JwtAuthenticationException extends AuthenticationException {
        public JwtAuthenticationException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public JwtAuthenticationException(String msg) {
            super(msg);
        }
    }
}
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

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey;
    private final long jwtExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
    }

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
            log.error("Erreur lors de la génération du token JWT", e);
            throw new JwtAuthenticationException("Impossible de générer le token");
        }
    }

    public String getEmailFromJWT(String token) {
        return parseToken(token).getSubject();
    }

    public UserRole getRoleFromJWT(String token) {
        Claims claims = parseToken(token);
        return UserRole.valueOf(claims.get(ROLE_CLAIM, String.class));
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (AuthenticationException ex) {
            log.debug("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token: {}", ex.getMessage());
            throw new JwtAuthenticationException("Token expiré", ex);
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}", ex.getMessage());
            throw new JwtAuthenticationException("Token non supporté", ex);
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            throw new JwtAuthenticationException("Token invalide", ex);
        } catch (SignatureException ex) {
            log.warn("JWT signature verification failed: {}", ex.getMessage());
            throw new JwtAuthenticationException("Signature invalide", ex);
        } catch (IllegalArgumentException ex) {
            log.warn("Empty or null JWT: {}", ex.getMessage());
            throw new JwtAuthenticationException("Token vide ou nul", ex);
        }
    }

    public static class JwtAuthenticationException extends AuthenticationException {
        public JwtAuthenticationException(String msg, Throwable cause) {
            super(msg, cause);
        }
        public JwtAuthenticationException(String msg) {
            super(msg);
        }
    }
}
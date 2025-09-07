package com.customworld.service.impl;

import com.customworld.dto.request.LoginRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.AuthResponse;
import com.customworld.entity.RefreshToken;
import com.customworld.entity.User;
import com.customworld.exception.BadRequestException;
import com.customworld.repository.RefreshTokenRepository;
import com.customworld.repository.UserRepository;
import com.customworld.security.JwtTokenProvider;
import com.customworld.service.AuthService;
import com.customworld.service.EmailService;
import com.customworld.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implémentation du service d'authentification.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                           RefreshTokenRepository refreshTokenRepository, EmailService emailService,
                           SmsService smsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Email ou mot de passe incorrect", ""));
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getRole());

        // Supprimer les anciens refresh tokens
       /*
        *         refreshTokenRepository.deleteByUser(user);
        // Enregistrer le nouveau refresh token
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getJwtRefreshExpiration()));
        refreshTokenRepository.save(refreshTokenEntity);

        */

    
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email déjà utilisé", "");
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());
        user.setRole(registerRequest.getRole());
        userRepository.save(user);

        // Envoyer un email de bienvenue
        try {
            emailService.sendEmail(
                    user.getEmail(),
                     "Bienvenue sur Custom W🌍rld", 
        "Bienvenue sur Custom W🌍rld, " + user.getName() + "\n\n Entrez dans l’univers de la customisation sans limites! Votre accessoire, votre style, votre signature. !\n\n Votre compte a été créé avec succès."
            );

        } catch (Exception e) {
            logger.error("Échec de l'envoi de l'email de bienvenue à {} : {}", user.getEmail(), e.getMessage());
            // L'échec de l'email n'est pas bloquant
        }

        // Envoyer un SMS de bienvenue (optionnel, si phoneNumber est disponible)
        if (registerRequest.getPhone() != null && !registerRequest.getPhone().isEmpty()) {
            try {
                smsService.sendSms(
                        registerRequest.getPhone(),
                        "Bienvenue sur Custom W🌍rld, " + user.getName() + "\n\n Entrez dans l’univers de la customisation sans limites! Votre accessoire, votre style, votre signature. !\n\n Votre compte a été créé avec succès."
                );
            } catch (Exception e) {
                logger.error("Échec de l'envoi du SMS de bienvenue à {} : {}", registerRequest.getPhone(), e.getMessage());
                // L'échec du SMS n'est pas bloquant
            }
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Refresh token invalide", ""));
        if (refreshTokenEntity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new BadRequestException("Refresh token expiré", "");
        }
        User user = refreshTokenEntity.getUser();

        // Supprimer l'ancien refresh token
        refreshTokenRepository.delete(refreshTokenEntity);

        // Générer de nouveaux tokens
        String newAccessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getRole());

        // Enregistrer le nouveau refresh token
        RefreshToken newRefreshTokenEntity = new RefreshToken();
        newRefreshTokenEntity.setUser(user);
        newRefreshTokenEntity.setToken(newRefreshToken);
        newRefreshTokenEntity.setExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getJwtRefreshExpiration()));
        refreshTokenRepository.save(newRefreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(user)
                .build();
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé", ""));
        String resetToken = jwtTokenProvider.generateResetToken(user.getEmail());
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusMillis(jwtTokenProvider.getJwtResetTokenExpiration()));
        userRepository.save(user);

        // Envoyer l'email de réinitialisation
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Réinitialisation de mot de passe",
                    "Bonjour " + user.getName() + ",\n\nCliquez sur ce lien pour réinitialiser votre mot de passe :https://customworld.vercel.app/reset-password?token=" + resetToken
            );
        } catch (Exception e) {
            logger.error("Échec de l'envoi de l'email de réinitialisation à {} : {}", user.getEmail(), e.getMessage());
            // L'échec de l'email n'est pas bloquant
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        if (!jwtTokenProvider.validateResetToken(token)) {
            throw new BadRequestException("Token de réinitialisation invalide ou expiré", "");
        }
        String email = jwtTokenProvider.getEmailFromJWT(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé", ""));
        if (!token.equals(user.getPasswordResetToken()) || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
            throw new BadRequestException("Token de réinitialisation invalide ou expiré", "");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }
}

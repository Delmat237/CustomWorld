package com.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;


public class UserInterceptor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserInterceptor.class);
    
    public static User getAuthenticatedUser(com.customworld.repository.UserRepository userRepository) {
    
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Utilisateur non authentifié");
            throw new ResourceNotFoundException("Utilisateur non authentifié");
        }

        String email = authentication.getName(); // Récupère l'email ou le nom d'utilisateur
        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Vendor not found with email: {}", email);
                    return new ResourceNotFoundException("Vendeur non trouvé");
                });

        return vendor;
            }
}

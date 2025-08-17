package com.customworld.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DotEnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(DotEnvConfig.class);

    @PostConstruct
    public void loadDotEnv() {
        try {
            logger.info("Tentative de chargement du fichier .env depuis le répertoire : {}", System.getProperty("user.dir"));
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                // Map JWT_SECRET or jwt.secret to jwt.secret
                if ("JWT_SECRET".equals(key) || "jwt.secret".equals(key)) {
                    System.setProperty("jwt.secret", value);
                    logger.debug("Variable .env convertie : jwt.secret={}", value);
                } else {
                    System.setProperty(key, value);
                    logger.debug("Variable .env chargée : {}={}", key, value);
                }
            });
            logger.info("Chargement du fichier .env terminé avec succès.");
            // Vérification spécifique pour jwt.secret
            String jwtSecret = System.getProperty("jwt.secret");
            if (jwtSecret == null) {
                logger.error("La propriété jwt.secret n'est pas définie dans les propriétés système.");
            } else {
                logger.info("jwt.secret chargé : {}", jwtSecret);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du fichier .env : {}", e.getMessage(), e);
        }
    }
}
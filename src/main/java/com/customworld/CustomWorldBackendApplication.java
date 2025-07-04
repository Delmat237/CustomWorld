package com.customworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.customworld.config.FileStorageConfig;

/**
 * Classe principale de l'application CustomWorld
 * Application de customisation d'accessoires
 */
@SpringBootApplication
@EnableConfigurationProperties(FileStorageConfig.class)
public class CustomWorldBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomWorldBackendApplication.class, args);
    }
}
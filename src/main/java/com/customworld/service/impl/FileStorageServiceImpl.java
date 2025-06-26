package com.customworld.service.impl;

import com.customworld.config.FileStorageConfig;
import com.customworld.exception.BadRequestException;
import com.customworld.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    /**
     * Implémentation du service de stockage de fichiers
     * Gère l'upload et la récupération des images et designs
     */
    private final Path fileStorageLocation;
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public FileStorageServiceImpl(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            if (fileName.contains("..")) {
                throw new BadRequestException("Nom de fichier invalide","");
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new BadRequestException("Impossible de stocker le fichier", e.getMessage());
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new BadRequestException("Fichier non trouvé: " + fileName,"");
            }
        } catch (MalformedURLException e) {
            throw new BadRequestException("Fichier non trouvé: " + fileName, e.getMessage());
        }
    }
}
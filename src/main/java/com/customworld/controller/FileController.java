package com.customworld.controller;

import com.customworld.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrôleur REST pour la gestion des opérations liées aux fichiers.
 * Fournit des endpoints pour le stockage et la récupération de fichiers.
 */
@RestController
@RequestMapping("/api/files")
@Tag(name="Fichier", description = "Fournit des endpoints pour le stockage et la récupération de fichiers." )
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Constructeur pour l'injection de dépendance du service de stockage.
     *
     * @param fileStorageService Service responsable du stockage physique des fichiers
     */
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Endpoint pour l'upload de fichiers.
     *
     * @param fichier Fichier multipart reçu dans la requête
     * @return ResponseEntity contenant l'URL publique d'accès au fichier
     */
    @PostMapping("/upload")
    @Operation(summary = "Endpoint pour l'upload de fichiers.")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile fichier) {
        String nomFichier = fileStorageService.storeFile(fichier);
        return ResponseEntity.ok("/api/files/" + nomFichier);
    }

    /**
     * Endpoint pour le téléchargement de fichiers.
     *
     * @param nomFichier Nom du fichier à récupérer (inclus dans le chemin d'URL)
     * @return ResponseEntity avec le fichier en flux et les en-têtes appropriés
     */
    @Operation(summary = "Endpoint pour le téléchargement de fichiers.")
    @GetMapping("/{nomFichier:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String nomFichier) {
        Resource fichier = fileStorageService.loadFileAsResource(nomFichier);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fichier.getFilename() + "\"")
                .body(fichier);
    }
}
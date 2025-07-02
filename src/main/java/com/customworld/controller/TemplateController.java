package com.customworld.controller;

import com.customworld.dto.request.TemplateRequest;
import com.customworld.dto.response.TemplateResponse;
import com.customworld.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des templates (modèles de personnalisation).
 * Fournit des endpoints pour créer, lire, mettre à jour et supprimer des templates.
 */
@RestController
@RequestMapping("/api/templates")
@Tag(name = "Templates", description = "API pour la gestion des templates (modèles de personnalisation)")
public class TemplateController {

    private final TemplateService templateService;

    /**
     * Constructeur avec injection du service TemplateService.
     *
     * @param templateService Service métier pour la gestion des opérations sur les templates.
     */
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Crée un nouveau template avec un fichier optionnel.
     *
     * @param request Données du template (nom, description, productId, createdById).
     * @param file Fichier optionnel (image ou design) associé au template.
     * @return ResponseEntity contenant le TemplateResponse du template créé.
     */
    @Operation(summary = "Créer un template", description = "Permet de créer un nouveau template avec un fichier optionnel (par exemple, une image de conception).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide (par exemple, fichier vide ou données manquantes)"),
            @ApiResponse(responseCode = "404", description = "Produit ou utilisateur non trouvé")
    })
    @PostMapping("/create")
    public ResponseEntity<TemplateResponse> createTemplate(
            @RequestPart("request") TemplateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(templateService.createTemplate(request, file));
    }

    /**
     * Récupère un template par son ID.
     *
     * @param id ID du template à récupérer.
     * @return ResponseEntity contenant le TemplateResponse du template.
     */
    @Operation(summary = "Récupérer un template par ID", description = "Récupère les détails d'un template spécifique en fonction de son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Template non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    /**
     * Récupère tous les templates ou ceux filtrés par produit ou utilisateur.
     *
     * @param productId ID du produit (optionnel).
     * @param createdById ID de l'utilisateur créateur (optionnel).
     * @return ResponseEntity contenant la liste des TemplateResponse.
     */
    @Operation(summary = "Récupérer tous les templates", description = "Récupère tous les templates ou ceux associés à un produit ou utilisateur spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des templates récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAllTemplates(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long createdById) {
        return ResponseEntity.ok(templateService.getAllTemplates(productId, createdById));
    }

    /**
     * Récupère les templates associés à un produit spécifique.
     *
     * @param productId ID du produit.
     * @return ResponseEntity contenant la liste des TemplateResponse.
     */
    @Operation(summary = "Récupérer les templates par produit", description = "Récupère tous les templates associés à un produit donné.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des templates récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<TemplateResponse>> getTemplatesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(templateService.getTemplatesByProduct(productId));
    }

    /**
     * Met à jour un template existant.
     *
     * @param id ID du template à mettre à jour.
     * @param request Données mises à jour du template.
     * @param file Fichier optionnel pour remplacer le fichier existant.
     * @return ResponseEntity contenant le TemplateResponse mis à jour.
     */
    @Operation(summary = "Mettre à jour un template", description = "Met à jour les informations d'un template existant, avec la possibilité de remplacer le fichier associé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Template non trouvé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable Long id,
            @RequestPart("request") TemplateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request, file));
    }

    /**
     * Supprime un template par son ID.
     *
     * @param id ID du template à supprimer.
     * @return ResponseEntity indiquant la réussite de la suppression.
     */
    @Operation(summary = "Supprimer un template", description = "Supprime un template spécifique en fonction de son ID, y compris le fichier associé si applicable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Template supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Template non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
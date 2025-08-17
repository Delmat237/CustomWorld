package com.customworld.service.impl;

import com.customworld.dto.request.TemplateRequest;
import com.customworld.dto.response.TemplateResponse;
import com.customworld.entity.Template;
import com.customworld.entity.Product;
import com.customworld.entity.User;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.TemplateRepository;
import com.customworld.repository.ProductRepository;
import com.customworld.repository.UserRepository;
import com.customworld.service.FileStorageService;
import com.customworld.service.TemplateService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des templates (modèles de personnalisation).
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;


    public TemplateServiceImpl(TemplateRepository templateRepository, ProductRepository productRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.templateRepository = templateRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Crée un nouveau template avec un fichier optionnel.
     *
     * @param request Données du template (nom, description, productId, createdById).
     * @param file Fichier optionnel associé au template.
     * @return TemplateResponse représentant le template créé.
     */
    @Override
    public TemplateResponse createTemplate(TemplateRequest request, MultipartFile file) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        User user = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Template template = new Template();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setProduct(product);
        template.setCreatedBy(user);
        if (file != null && !file.isEmpty()) {
            template.setTemplatePath(fileStorageService.storeFile(file));
        }

        template = templateRepository.save(template);
        return convertToTemplateResponse(template);
    }

    /**
     * Récupère un template par son ID.
     *
     * @param id ID du template.
     * @return TemplateResponse représentant le template.
     * @throws ResourceNotFoundException si le template n'existe pas.
     */
    @Override
    public TemplateResponse getTemplateById(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template non trouvé"));
        return convertToTemplateResponse(template);
    }

    /**
     * Récupère tous les templates, avec filtres optionnels par produit ou utilisateur.
     *
     * @param productId ID du produit (optionnel).
     * @param createdById ID de l'utilisateur créateur (optionnel).
     * @return Liste de TemplateResponse.
     */
    @Override
    public List<TemplateResponse> getAllTemplates(Long productId, Long createdById) {
        List<Template> templates;
        if (productId != null && createdById != null) {
            templates = templateRepository.findByProductIdAndCreatedById(productId, createdById);
        } else if (productId != null) {
            templates = templateRepository.findByProductId(productId);
        } else if (createdById != null) {
            templates = templateRepository.findByCreatedById(createdById);
        } else {
            templates = templateRepository.findAll();
        }
        return templates.stream().map(this::convertToTemplateResponse).collect(Collectors.toList());
    }

    /**
     * Récupère les templates associés à un produit.
     *
     * @param productId ID du produit.
     * @return Liste de TemplateResponse.
     * @throws ResourceNotFoundException si le produit n'existe pas.
     */
    @Override
    public List<TemplateResponse> getTemplatesByProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        List<Template> templates = templateRepository.findByProductId(productId);
        return templates.stream().map(this::convertToTemplateResponse).collect(Collectors.toList());
    }

    /**
     * Met à jour un template existant.
     *
     * @param id ID du template à mettre à jour.
     * @param request Données mises à jour du template.
     * @param file Fichier optionnel pour remplacer le fichier existant.
     * @return TemplateResponse représentant le template mis à jour.
     * @throws ResourceNotFoundException si le template ou les entités associées n'existent pas.
     */
    @Override
    public TemplateResponse updateTemplate(Long id, TemplateRequest request, MultipartFile file) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template non trouvé"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        User user = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setProduct(product);
        template.setCreatedBy(user);
        if (file != null && !file.isEmpty()) {
            // Supprimer l'ancien fichier si nécessaire
            if (template.getTemplatePath() != null) {
                fileStorageService.deleteFile(template.getTemplatePath());
            }
            template.setTemplatePath(fileStorageService.storeFile(file));
        }

        template = templateRepository.save(template);
        return convertToTemplateResponse(template);
    }

    /**
     * Supprime un template par son ID.
     *
     * @param id ID du template à supprimer.
     * @throws ResourceNotFoundException si le template n'existe pas.
     */
    @Override
    public void deleteTemplate(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template non trouvé"));
        if (template.getTemplatePath() != null) {
            fileStorageService.deleteFile(template.getTemplatePath());
        }
        templateRepository.delete(template);
    }

    /**
     * Convertit une entité Template en TemplateResponse.
     *
     * @param template Entité Template.
     * @return TemplateResponse pour la réponse API.
     */
    private TemplateResponse convertToTemplateResponse(Template template) {
        TemplateResponse response = new TemplateResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setProductId(template.getProduct().getId());
        response.setCreatedById(template.getCreatedBy().getId());
        response.setTemplatePath(template.getTemplatePath());
        return response;
    }
}
package com.customworld.controller;

import com.customworld.dto.response.ApiResponseWrapper;
import com.customworld.service.EmailService;
import com.customworld.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur REST pour la gestion des notifications (email, SMS, etc.).
 * Fournit des endpoints pour envoyer des notifications aux utilisateurs.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "API pour l'envoi de notifications par email, SMS, etc.")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final EmailService emailService;
    private final SmsService smsService;

    /**
     * Constructeur avec injection des services de notification.
     *
     * @param emailService Service pour l'envoi d'emails.
     * @param smsService Service pour l'envoi de SMS.
     */
    public NotificationController(EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    /**
     * Envoie une notification par email à un utilisateur.
     *
     * @param request Map contenant l'email, le sujet et le message.
     * @return ResponseEntity contenant un message de succès ou d'erreur.
     */
    @Operation(summary = "Envoyer une notification par email", description = "Envoie un email à l'adresse spécifiée avec un sujet et un message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (email, sujet ou message manquant)"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de l'envoi de l'email")
    })
    @PostMapping("/send-email")
    public ResponseEntity<ApiResponseWrapper> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String subject = request.get("subject");
        String message = request.get("message");

        if (email == null || subject == null || message == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseWrapper(false, "Email, sujet ou message manquant"));
        }

        try {
            emailService.sendEmail(email, subject, message);
            return ResponseEntity.ok(new ApiResponseWrapper(true, "Email envoyé avec succès"));
        } catch (Exception e) {
            logger.error("Échec de l'envoi de l'email à {} : {}", email, e.getMessage());
            return ResponseEntity.status(500)
                    .body(new ApiResponseWrapper(false, "Erreur lors de l'envoi de l'email"));
        }
    }

    /**
     * Envoie une notification par SMS à un utilisateur.
     *
     * @param request Map contenant le numéro de téléphone et le message.
     * @return ResponseEntity contenant un message de succès ou d'erreur.
     */
    @Operation(summary = "Envoyer une notification par SMS", description = "Envoie un SMS au numéro spécifié avec un message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SMS envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (numéro de téléphone ou message manquant)"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de l'envoi du SMS")
    })
    @PostMapping("/send-sms")
    public ResponseEntity<ApiResponseWrapper> sendSms(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String message = request.get("message");

        if (phone == null || message == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseWrapper(false, "Numéro de téléphone ou message manquant"));
        }

        try {
            smsService.sendSms(phone, message);
            return ResponseEntity.ok(new ApiResponseWrapper(true, "SMS envoyé avec succès"));
        } catch (Exception e) {
            logger.error("Échec de l'envoi du SMS à {} : {}", phone, e.getMessage());
            return ResponseEntity.status(500)
                    .body(new ApiResponseWrapper(false, "Erreur lors de l'envoi du SMS"));
        }
    }
}
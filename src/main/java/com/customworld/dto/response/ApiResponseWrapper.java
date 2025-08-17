package com.customworld.dto.response;

import lombok.*;

/**
 * DTO représentant une réponse générique d'API.
 * Contient un indicateur de succès et un message associé.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper {

    private boolean success;
    private String message;
}
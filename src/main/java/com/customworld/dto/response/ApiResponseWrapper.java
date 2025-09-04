package com.customworld.dto.response;

import lombok.*;

/**
 * DTO représentant une réponse générique d'API.
 * Contient un indicateur de succès et un message associé.
 */

@Data
@Builder
@NoArgsConstructor

public class ApiResponseWrapper {

    private boolean success;
    private String message;

    public ApiResponseWrapper(boolean success,String message) {
        this.message = message;
        this.success = success;
    }
}
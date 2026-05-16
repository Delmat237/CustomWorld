package com.customworld.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {

    @NotBlank(message = "Le contenu de l'avis ne peut pas être vide")
    private String content;
}

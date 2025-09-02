package com.customworld.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String status;
    private String message;
    private Integer code;
    private Object transaction; // Pour capturer l'objet transaction complet
    private String authorization_url; // Pour capturer l'URL d'autorisation
}

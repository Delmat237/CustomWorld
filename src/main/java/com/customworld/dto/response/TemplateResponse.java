package com.customworld.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateResponse {
    private Long id;
    private String name;
    private String description;
    private Long productId;
    private Long createdById;
    private String templatePath;
}
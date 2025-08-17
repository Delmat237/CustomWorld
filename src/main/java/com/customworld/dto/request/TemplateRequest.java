package com.customworld.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateRequest {
    private String name;
    private String description;
    private Long productId;
    private Long createdById;
}
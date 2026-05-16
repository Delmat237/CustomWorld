package com.customworld.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long productId;
    private Long userId;
    private String userName;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}

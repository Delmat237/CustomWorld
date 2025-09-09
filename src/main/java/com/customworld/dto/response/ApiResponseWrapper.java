package com.customworld.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic wrapper for API responses to standardize success/failure responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;

    // Constructor for success/failure without data
    public ApiResponseWrapper(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
}
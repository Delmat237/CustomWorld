package com.customworld.exception;

import com.customworld.dto.response.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponseWrapper(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseWrapper> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new ApiResponseWrapper(false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(new ApiResponseWrapper(false, "Une erreur s'est produite="+ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
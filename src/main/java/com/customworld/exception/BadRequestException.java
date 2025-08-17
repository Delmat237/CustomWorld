package com.customworld.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, String e) {
        super(message);
    }
}
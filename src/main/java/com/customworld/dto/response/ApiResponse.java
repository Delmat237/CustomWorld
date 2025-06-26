package com.customworld.dto.response;

/**
 * DTO représentant une réponse générique d'API.
 * Contient un indicateur de succès et un message associé.
 */
public class ApiResponse {

    private boolean success;
    private String message;

    /**
     * Constructeur sans argument.
     */
    public ApiResponse() {
    }

    /**
     * Constructeur complet.
     *
     * @param success Indique si l'opération a réussi.
     * @param message Message associé à la réponse.
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters et Setters explicites

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Builder statique pour faciliter la création d'instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String message;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(success, message);
        }
    }
}

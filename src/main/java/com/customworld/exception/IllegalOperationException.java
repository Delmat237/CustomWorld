package com.customworld.exception;

/**
 * Exception lancée lorsqu'une opération métier invalide est tentée.
 * Utilisée pour signaler des violations des règles métier plutôt que des erreurs techniques.
 */
public class IllegalOperationException extends RuntimeException {

    /**
     * Constructeur avec un message d'erreur.
     *
     * @param message Description détaillée de l'erreur métier
     */
    public IllegalOperationException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause sous-jacente.
     *
     * @param message Description de l'erreur
     * @param cause Exception originale ayant causé cette erreur
     */
    public IllegalOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur avec formattage de message.
     *
     * @param format Format du message (comme String.format)
     * @param args Arguments pour le formatage
     */
    public IllegalOperationException(String format, Object... args) {
        super(String.format(format, args));
    }
}
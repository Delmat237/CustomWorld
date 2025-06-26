package com.customworld.enums;

/**
 * Énumération des statuts de commande
 */
public enum OrderStatus {
    PENDING,        // En attente
    CONFIRMED,      // Confirmée
    IN_PROGRESS,    // En cours de customisation
    COMPLETED,      // Terminée
    SHIPPED,        // Expédiée
    DELIVERED,      // Livrée
    CANCELLED  ;    // Annulée

    public boolean canBeCancelled() {
        return this == PENDING || this == IN_PROGRESS;
    }
}

package com.customworld.enums;

public enum DeliveryStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    DELIVERED,
    CANCELLED,
    ISSUE_REPORTED;

    public boolean canTransitionTo(DeliveryStatus newStatus) {
        // Implémenter la logique métier des transitions autorisées
        return switch (this) {
            case PENDING -> newStatus == ASSIGNED || newStatus == CANCELLED;
            case ASSIGNED -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS -> newStatus == DELIVERED || newStatus == ISSUE_REPORTED;
            default -> false;
        };
    }

    public boolean isActiveStatus() {
        return this == IN_PROGRESS || this == ASSIGNED;
    }
}
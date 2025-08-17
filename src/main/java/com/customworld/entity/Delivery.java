package com.customworld.entity;

import com.customworld.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Représente une livraison associée à une commande.
 * Gère le processus d'expédition et de suivi.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Commande associée à cette livraison.
     * Relation many-to-one: une livraison par commande.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private CustomOrder order;

    /**
     * Livreur assigné pour la livraison.
     * Doit avoir le rôle DELIVERER dans le système.
     */
    @ManyToOne
    @JoinColumn(name = "deliverer_id")
    private User deliverer;

    /**
     * État actuel de la livraison (ex: PENDING, IN_TRANSIT, DELIVERED)
     */
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(length = 500)
    private String issueDescription;

    private LocalDateTime deliveryDate;


}

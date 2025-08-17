package com.customworld.entity;

import com.customworld.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Représente une commande client dans le système.
 * Contient toutes les informations relatives à une transaction commerciale.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "custom_orders")
public class CustomOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Client associé à la commande.
     * Relation many-to-one: plusieurs commandes peuvent appartenir à un client.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    /**
     * Liste des articles dans la commande.
     * Cascade : les opérations sur la commande affectent ses articles.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    /**
     * État actuel de la commande (ex: CREATED, PAID, SHIPPED)
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * Date/heure de création de la commande.
     * Valeur par défaut: timestamp de création
     */
    private LocalDateTime orderDate;

    /**
     * Adresse de livraison complète
     */
    private String deliveryAddress;


}

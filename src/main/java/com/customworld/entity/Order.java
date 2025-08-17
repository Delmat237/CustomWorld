package com.customworld.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

import com.customworld.enums.OrderStatus;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Double amount;

    private String currency;

    private String transactionId;

    private OrderStatus status; // PENDING, COMPLETED, FAILED

    private Instant createdAt;

    private Instant updatedAt;
}
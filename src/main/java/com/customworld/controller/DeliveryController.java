package com.customworld.controller;

import com.customworld.dto.response.DeliveryResponse;
import com.customworld.entity.Delivery;
import com.customworld.entity.User;
import com.customworld.service.DeliveryService;
import com.utils.UserInterceptor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.customworld.repository.UserRepository;
import com.customworld.entity.User;


import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing delivery operations.
 * Provides endpoints for retrieving and updating delivery information.
 */
@Tag(name="Livreur ", description = "Provides endpoints for retrieving and updating delivery information.")
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final UserRepository userRepository;

    /**
     * Constructs a DeliveryController with required dependencies.
     *
     * @param deliveryService Service layer component for delivery operations
     */
    public DeliveryController(DeliveryService deliveryService, UserRepository userRepository) {
        this.deliveryService = deliveryService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all deliveries associated with a specific deliverer.
     *
     * @param delivererId ID of the deliverer to fetch deliveries for
     * @return ResponseEntity containing list of DeliveryResponse objects
     */
    @Operation(summary = "Retrieves all deliveries associated with a specific deliverer.")
    @GetMapping("/deliveries")
    public ResponseEntity<List<DeliveryResponse>> getDelivererDeliveries() {
        User user = UserInterceptor.getAuthenticatedUser(userRepository);
        Long delivererId = user.getId();
        List<Delivery> deliveries = deliveryService.getDeliveriesByDeliverer(delivererId);
        List<DeliveryResponse> response = deliveries.stream().map(this::convertToDeliveryResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the status of a specific delivery.
     *
     * @param id ID of the delivery to update
     * @param status New status value to set
     * @return ResponseEntity with updated DeliveryResponse
     */
    @Operation(summary = "Updates the status of a specific delivery.")
    @PutMapping("/deliveries/{id}/status")
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Delivery delivery = deliveryService.updateDeliveryStatus(id, status);
        return ResponseEntity.ok(convertToDeliveryResponse(delivery));
    }

    /**
     * Converts a Delivery entity to its DTO representation.
     *
     * @param delivery Entity to convert
     * @return Corresponding DTO response object
     */
    private DeliveryResponse convertToDeliveryResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setOrderId(delivery.getOrder().getId());
        response.setDelivererId(delivery.getDeliverer().getId());
        response.setStatus(delivery.getStatus());
        return response;
    }
}
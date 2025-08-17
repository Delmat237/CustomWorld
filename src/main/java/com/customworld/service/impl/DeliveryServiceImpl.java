package com.customworld.service.impl;

import com.customworld.dto.response.DeliveryResponse;
import com.customworld.entity.Delivery;
import com.customworld.enums.DeliveryStatus;
import com.customworld.exception.IllegalOperationException;
import com.customworld.exception.ResourceNotFoundException;
import com.customworld.repository.DeliveryRepository;
import com.customworld.service.DeliveryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional

public  class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public DeliveryServiceImpl(
                            DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }
    @Override
    public List<Delivery> getDeliveriesByDeliverer(Long delivererId) {
        log.info("Fetching deliveries for deliverer ID: {}", delivererId);
        return deliveryRepository.findByDelivererId(delivererId);
    }

    @Override
    public Delivery updateDeliveryStatus(Long id, String status) {
        Delivery delivery = getDeliveryOrThrow(id);
        DeliveryStatus newStatus = parseDeliveryStatus(status);

        if (!delivery.getStatus().canTransitionTo(newStatus)) {
            log.warn("Invalid status transition from {} to {} for delivery {}",
                    delivery.getStatus(), newStatus, id);
            throw new IllegalOperationException("Transition de statut non autorisée");
        }

        delivery.setStatus(newStatus);
        Delivery updated = deliveryRepository.save(delivery);
        log.info("Updated delivery {} status to {}", id, newStatus);

        return updated;
    }

    @Override
    public List<DeliveryResponse> getDeliveryPersonAssignments() {
        log.info("Fetching all delivery assignments");
        return deliveryRepository.findAllWithOrderAndDeliverer().stream()
                .map(this::convertToDeliveryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryResponse> getDeliveriesByStatus(String status) {
        DeliveryStatus deliveryStatus = parseDeliveryStatus(status);
        log.info("Fetching deliveries with status: {}", deliveryStatus);

        return deliveryRepository.findByStatus(deliveryStatus).stream()
                .map(this::convertToDeliveryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeliveryResponse acceptDelivery(Long deliveryId) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);

        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            log.warn("Delivery {} cannot be accepted in current status: {}",
                    deliveryId, delivery.getStatus());
            throw new IllegalOperationException("Seules les livraisons PENDING peuvent être acceptées");
        }

        delivery.setStatus(DeliveryStatus.ASSIGNED);
        deliveryRepository.save(delivery);
        log.info("Delivery {} accepted by deliverer {}", deliveryId, delivery.getDeliverer().getId());

        return convertToDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse startDelivery(Long deliveryId) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);

        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            log.warn("Delivery {} cannot be started in current status: {}",
                    deliveryId, delivery.getStatus());
            throw new IllegalOperationException("Seules les livraisons ASSIGNED peuvent être démarrées");
        }

        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        deliveryRepository.save(delivery);
        log.info("Delivery {} started", deliveryId);

        return convertToDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse completeDelivery(Long deliveryId) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);

        if (delivery.getStatus() != DeliveryStatus.IN_PROGRESS) {
            log.warn("Delivery {} cannot be completed in current status: {}",
                    deliveryId, delivery.getStatus());
            throw new IllegalOperationException("Seules les livraisons IN_PROGRESS peuvent être complétées");
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);
        log.info("Delivery {} completed successfully", deliveryId);

        return convertToDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse reportDeliveryIssue(Long deliveryId, String issue) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);

        if (!delivery.getStatus().isActiveStatus()) {
            log.warn("Cannot report issue for delivery {} in status {}",
                    deliveryId, delivery.getStatus());
            throw new IllegalOperationException("Problème ne peut être signalé que pour les livraisons actives");
        }

        delivery.setStatus(DeliveryStatus.ISSUE_REPORTED);
        delivery.setIssueDescription(issue);
        deliveryRepository.save(delivery);
        log.warn("Issue reported for delivery {}: {}", deliveryId, issue);

        return convertToDeliveryResponse(delivery);
    }

    @Override
    public List<DeliveryResponse> getDeliveryHistory() {
        log.info("Fetching complete delivery history");
        return deliveryRepository.findAllByOrderByDeliveryDateDesc().stream()
                .map(this::convertToDeliveryResponse)
                .collect(Collectors.toList());
    }

    // ======= HELPER METHODS ======= //

    private Delivery getDeliveryOrThrow(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Delivery not found: {}", id);
                    return new ResourceNotFoundException("Livraison non trouvée");
                });
    }

    private DeliveryStatus parseDeliveryStatus(String status) {
        try {
            return DeliveryStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid delivery status: {}", status);
            throw new IllegalArgumentException("Statut de livraison invalide: " + status);
        }
    }

    private DeliveryResponse convertToDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrder().getId())
                .delivererId(delivery.getDeliverer().getId())
                .status(delivery.getStatus())
                .issueDescription(delivery.getIssueDescription())
                .deliveryDate(delivery.getDeliveryDate())
                .build();
    }
}
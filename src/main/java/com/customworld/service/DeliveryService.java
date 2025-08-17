package com.customworld.service;

import com.customworld.dto.response.DeliveryResponse;
import com.customworld.entity.Delivery;

import java.util.List;

public interface DeliveryService {
    List<Delivery> getDeliveriesByDeliverer(Long delivererId);
    Delivery updateDeliveryStatus(Long id, String status);


    List<DeliveryResponse> getDeliveryPersonAssignments();

    List<DeliveryResponse> getDeliveriesByStatus(String status);
    DeliveryResponse acceptDelivery(Long deliveryId);
    DeliveryResponse startDelivery(Long deliveryId);
    //DeliveryResponse completeDelivery(Long deliveryId, String status);

    DeliveryResponse completeDelivery(Long deliveryId);

    DeliveryResponse reportDeliveryIssue(Long deliveryId, String issue);
    List<DeliveryResponse> getDeliveryHistory();
}
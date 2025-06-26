package com.customworld.repository;

import com.customworld.entity.Delivery;
import com.customworld.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByDelivererId(Long delivererId);
    @Query("SELECT d FROM Delivery d JOIN FETCH d.order JOIN FETCH d.deliverer")
    List<Delivery> findAllWithOrderAndDeliverer();

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findAllByOrderByDeliveryDateDesc();
}
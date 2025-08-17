package com.customworld.repository;

import com.customworld.entity.CustomOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {
    List<CustomOrder> findByCustomerId(Long customerId);
}
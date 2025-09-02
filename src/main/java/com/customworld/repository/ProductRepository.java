package com.customworld.repository;

import com.customworld.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByVendorId(Long vendorId);
    List<Product> findByCategoryName(String categoryName);
    Optional<Product> findById(Long id);
    
}

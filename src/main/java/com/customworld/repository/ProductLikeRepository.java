package com.customworld.repository;

import com.customworld.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    Optional<ProductLike> findByProductIdAndUserId(Long productId, Long userId);
    long countByProductId(Long productId);
    void deleteByProductIdAndUserId(Long productId, Long userId);
}

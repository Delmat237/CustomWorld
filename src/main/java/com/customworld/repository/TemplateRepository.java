package com.customworld.repository;

import com.customworld.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByProductId(Long productId);
    List<Template> findByCreatedById(Long createdById);
    List<Template> findByProductIdAndCreatedById(Long productId, Long createdById);
}
package com.customworld.service;

import com.customworld.dto.request.TemplateRequest;
import com.customworld.dto.response.TemplateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TemplateService {

     TemplateResponse createTemplate(TemplateRequest request, MultipartFile file) ;
     TemplateResponse getTemplateById(Long id) ;
     List<TemplateResponse> getAllTemplates(Long productId, Long createdById) ;
     List<TemplateResponse> getTemplatesByProduct(Long productId) ;
     TemplateResponse updateTemplate(Long id, TemplateRequest request, MultipartFile file) ;
     void deleteTemplate(Long id);

}
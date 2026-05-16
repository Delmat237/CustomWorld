package com.customworld.service;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface FileStorageService {
    String storeFile(MultipartFile file);
    Mono<String> storeFile(FilePart file);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
}

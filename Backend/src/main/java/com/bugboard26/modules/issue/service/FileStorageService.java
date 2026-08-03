package com.bugboard26.modules.issue.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
     
    String uploadImage(MultipartFile file);
}

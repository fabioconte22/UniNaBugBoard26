package com.bugboard26.modules.issue.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {
    
    private final Path uploadDirectory = Paths.get("uploads/issues");

    public LocalFileStorageService() {
        try {
            Files.createDirectories(uploadDirectory);
        }catch(IOException e) {
            throw new IllegalArgumentException("Impossibile inizializzare la cartella di upload!", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file){
        if(file.isEmpty()) {
            throw new IllegalArgumentException("Impossibile caricare un file vuoto!");
        }

        try {
            String originalFileName = file.getOriginalFilename(); 
            String extension = originalFileName != null && originalFileName.contains(".")
                                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                                : ".jpg";
            String uniqueFilename = UUID.randomUUID().toString() + extension; 
            Path destinationPath = this.uploadDirectory.resolve(uniqueFilename).normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            return "/upload/issues/" + uniqueFilename; 

        } catch(IOException e) {
            throw new RuntimeException("Errore durante il salvataggio del file!", e);
        }
          
    }
}
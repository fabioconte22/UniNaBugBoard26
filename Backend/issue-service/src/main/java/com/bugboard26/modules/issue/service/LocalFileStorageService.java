package com.bugboard26.modules.issue.service;

import com.bugboard26.shared.exception.InvalidImageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    private static final String PUBLIC_PREFIX = "/uploads/issues/";
    
    private final Path uploadDirectory = Paths.get("uploads/issues").toAbsolutePath().normalize();

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
            throw new InvalidImageException("Il file è vuoto!");
        }

        try {
            byte[] content = file.getBytes();

            String extension = detectImageExtension(content);

            if(extension == null) {
                throw new InvalidImageException("Formato non supportato: sono ammessi PNG, JPEG, GIF e WebP.");
            }

            String uniqueFilename =UUID.randomUUID() + extension; 
            Files.write(uploadDirectory.resolve(uniqueFilename), content);

            return PUBLIC_PREFIX + uniqueFilename; 

        } catch(IOException e) {
            throw new RuntimeException("Errore durante il salvataggio del file!", e);
        }
          
    }

    @Override 
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        Path target = uploadDirectory.resolve(imageUrl.substring(PUBLIC_PREFIX.length())).normalize();

        if (!target.startsWith(uploadDirectory)) {
            return; 
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Impossibile eliminare l'immagine precedente {}", target, e);
        }
    }

    private static String detectImageExtension(byte[] data) {
        if (startsWith(data, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) return ".png";
        if (startsWith(data, 0xFF, 0xD8, 0xFF)) return ".jpg";
        if (startsWith(data, 'G', 'I', 'F', '8')) return ".gif";
        if (startsWith(data, 'R', 'I', 'F', 'F') && data.length >= 12
                && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') return ".webp";
        return null;
    }

    private static boolean startsWith(byte[] data, int... signature) {
        if (data.length < signature.length) {
            return false;
        }
        for (int i = 0; i< signature.length; i++) {
            if ((data[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true; 
    }
}
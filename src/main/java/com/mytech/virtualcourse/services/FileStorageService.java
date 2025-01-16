// src/main/java/com/mytech/virtualcourse/services/FileStorageService.java

package com.mytech.virtualcourse.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();


            Files.createDirectories(uploadPath);


            Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/" + file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }
}

package com.mytech.virtualcourse.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Store a file in the root upload directory
     * @param file The file to store
     * @return The URL path to the stored file
     */
    public String storeFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Store a file in a specific subdirectory with a unique filename
     * @param file The file to store
     * @param subDirectory The subdirectory to store the file in
     * @return The name of the stored file
     */
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // Create the directory if it doesn't exist
        Path directoryPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Generate a unique file name
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Copy the file to the target location
        Path targetLocation = directoryPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    /**
     * Get the URL for a stored file
     * @param subDirectory The subdirectory where the file is stored
     * @param fileName The name of the file
     * @return The URL path to the file
     */
    public String getFileUrl(String subDirectory, String fileName) {
        return "/uploads/" + subDirectory + "/" + fileName;
    }
}
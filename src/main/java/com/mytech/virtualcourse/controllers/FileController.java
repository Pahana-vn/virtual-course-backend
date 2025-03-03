package com.mytech.virtualcourse.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final String BASE_UPLOAD_DIR = "src/main/resources/uploads/";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        String uploadDir = BASE_UPLOAD_DIR + type + "/";
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error creating directory: " + uploadDir);
            }
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        executorService.submit(() -> {
            try {
                Files.copy(file.getInputStream(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        String fileUrl = "http://localhost:8080/uploads/" + type + "/" + fileName;
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/exists/{type}/{filename}")
    public ResponseEntity<Boolean> checkFileExists(
            @PathVariable String type,
            @PathVariable String filename) {
        String filePath = BASE_UPLOAD_DIR + type + "/" + filename;
        File file = new File(filePath);

        if (file.exists()) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{type}/{filename}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable String type,
            @PathVariable String filename) {
        try {
            String filePath = BASE_UPLOAD_DIR + type + "/" + filename;
            Path path = Paths.get(filePath);
            byte[] fileContent = Files.readAllBytes(path);
            return ResponseEntity.ok().body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
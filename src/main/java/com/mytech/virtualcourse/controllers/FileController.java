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

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final String BASE_UPLOAD_DIR = "src/main/resources/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type)  {
        try {
            // Xác định thư mục upload dựa trên type
            String uploadDir = BASE_UPLOAD_DIR + type + "/";
            File dir = new File(uploadDir);

            if (!dir.exists()) {
                boolean created = dir.mkdirs();  // Tạo thư mục nếu chưa có
                if (!created) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error creating directory: " + uploadDir);
                }
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath);

            // Trả về đường dẫn URL
            String fileUrl = "http://localhost:8080/uploads/" + type + "/" + fileName;  // Đường dẫn tương đối
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }
}

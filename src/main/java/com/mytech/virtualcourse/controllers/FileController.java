// src/main/java/com/mytech/virtualcourse/controllers/FileController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000") // Đảm bảo CORS được cấu hình đúng
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Upload ảnh cho các thực thể (instructor, course, student).
     * @param entity - Loại thực thể (instructor, course, student).
     * @param file - File hình ảnh.
     * @return Tên file đã lưu.
     */
    @PostMapping("/upload/{entity}")
    public ResponseEntity<String> uploadPhoto(@PathVariable String entity,@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file, entity);
            return new ResponseEntity<>(fileName, HttpStatus.OK);
        } catch (Exception e) {
            // Log lỗi chi tiết
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

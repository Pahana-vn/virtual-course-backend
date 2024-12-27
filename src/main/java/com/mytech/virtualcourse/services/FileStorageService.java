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

    /**
     * Lưu file và trả về tên file đã lưu.
     * @param file - File hình ảnh.
     * @return Tên file đã lưu.
     */
    public String storeFile(MultipartFile file, String entity) {
        try {
            // Kiểm tra entity hợp lệ
            if (!entity.equals("instructor") &&
                    !entity.equals("course") &&
                    !entity.equals("student") &&
                    !entity.equals("category")) {
                throw new RuntimeException("Invalid entity type for file upload.");
            }

            // Thay vì chỉ lấy uploadDir, ta cần nối thêm entity:
            Path entityUploadPath = Paths.get(uploadDir, entity).toAbsolutePath().normalize();

            // Tạo directory nếu chưa có
            Files.createDirectories(entityUploadPath);

            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String newFileName = System.currentTimeMillis() + "_" + originalFileName;
            // Lưu file vào đường dẫn `uploads/<entity>/<newFileName>`
            Path filePath = entityUploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFileName; // Trả về tên file đã lưu
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not store file " + file.getOriginalFilename() + ". Please try again!",
                    e
            );
        }
    }
    /**
     * Thêm phương thức xóa file cũ
     * @param fileName Tên file cũ đang lưu trong DB (VD: "1691234567890_myphoto.png")
     * @param entity  Thư mục con (category, instructor, ...)
     */
    public void deleteFile(String fileName, String entity) {
        if (fileName == null || fileName.isEmpty()) {
            return; // Không có gì để xóa
        }

        // Kiểm tra entity hợp lệ
        if (!entity.equals("instructor") &&
                !entity.equals("course") &&
                !entity.equals("student") &&
                !entity.equals("category")) {
            return;
        }

        // Tạo đường dẫn tuyệt đối: uploads/<entity>/<fileName>
        Path entityUploadPath = Paths.get(uploadDir, entity).toAbsolutePath().normalize();
        Path filePath = entityUploadPath.resolve(fileName);

        try {
            // Xóa file nếu tồn tại
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Xử lý nếu cần, hoặc ghi log
            throw new RuntimeException("Could not delete file " + fileName, e);
        }
    }

}

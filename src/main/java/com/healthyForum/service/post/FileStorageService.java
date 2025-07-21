package com.healthyForum.service.post;

import com.healthyForum.model.User;
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

    private final String UPLOAD_DIR = "uploads";

    public String save(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File is empty or null");
            }

            String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = UUID.randomUUID() + "_" + originalName;

            // Ensure path is resolved relative to the project root directory
            Path projectRoot = Paths.get("").toAbsolutePath(); // safest way to get project root
            Path uploadPath = projectRoot.resolve(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return path for use in HTML (frontend)
            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file", e);
        }
    }

    public String save(MultipartFile file, String username) {
        try {
            String originalName = file.getOriginalFilename();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
            String timestamp = java.time.LocalDateTime.now().format(formatter);
            String fileName = username + "_" + timestamp + "_" + originalName;

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file", e);
        }
    }
}

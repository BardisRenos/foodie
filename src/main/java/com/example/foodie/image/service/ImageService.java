package com.example.foodie.image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String upload(MultipartFile file, String folder) {
        try {
            validateFile(file);

            Path uploadPath = Paths.get(uploadDir, folder);
            Files.createDirectories(uploadPath);

            String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            String url = baseUrl + "/uploads/" + folder + "/" + filename;
            log.info("Uploaded image: {}", url);
            return url;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    public void delete(String imageUrl) {
        try {
            String relativePath = imageUrl.replace(baseUrl + "/", "");
            Path filePath = Paths.get(relativePath);
            Files.deleteIfExists(filePath);
            log.info("Deleted image: {}", imageUrl);
        } catch (IOException e) {
            log.error("Failed to delete image: {}", e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File is empty");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 5MB");
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot) : ".jpg";
    }
}
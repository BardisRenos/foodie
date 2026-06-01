package com.example.foodie.image.controller;

import com.example.foodie.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
@PreAuthorize("hasRole('FARMER')")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "products") String folder) {
        String url = imageService.upload(file, folder);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String imageUrl) {
        imageService.delete(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
package com.example.foodie.review.controller;

import com.example.foodie.review.dto.ReviewDto;
import com.example.foodie.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto.ReviewResponse> create(@Valid @RequestBody ReviewDto.CreateRequest request) {
        return ResponseEntity.ok(reviewService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto.ReviewResponse>> list(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String farmerId) {
        if (productId != null) return ResponseEntity.ok(reviewService.findByProduct(productId));
        if (farmerId != null) return ResponseEntity.ok(reviewService.findByFarmer(farmerId));
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/rating")
    public ResponseEntity<Double> avgRating(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String farmerId) {
        if (productId != null) return ResponseEntity.ok(reviewService.avgRatingForProduct(productId));
        if (farmerId != null) return ResponseEntity.ok(reviewService.avgRatingForFarmer(farmerId));
        return ResponseEntity.badRequest().build();
    }
}

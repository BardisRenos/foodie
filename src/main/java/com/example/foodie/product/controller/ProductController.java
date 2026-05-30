package com.example.foodie.product.controller;

import com.example.foodie.product.dto.ProductDto;
import com.example.foodie.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto.ProductResponse> create(@Valid @RequestBody ProductDto.CreateRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto.ProductResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String farmerId) {
        if (search != null) return ResponseEntity.ok(productService.search(search));
        if (category != null) return ResponseEntity.ok(productService.findByCategory(category));
        if (farmerId != null) return ResponseEntity.ok(productService.findByFarmer(farmerId));
        return ResponseEntity.ok(productService.findAvailable());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}

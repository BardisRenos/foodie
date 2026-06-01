package com.example.foodie.product.controller;

import com.example.foodie.product.dto.ProductDto;
import com.example.foodie.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String farmerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        if (search != null)
            return ResponseEntity.ok(productService.searchPaged(search, page, size));
        if (category != null)
            return ResponseEntity.ok(productService.findByCategoryPaged(category, page, size));
        if (farmerId != null)
            return ResponseEntity.ok(productService.findByFarmer(farmerId));
        return ResponseEntity.ok(productService.findAvailablePaged(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto.ProductResponse> update(@PathVariable String id, @Valid @RequestBody ProductDto.CreateRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ProductDto.ProductResponse> toggleAvailability(@PathVariable String id) {
        return ResponseEntity.ok(productService.toggleAvailability(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto.ProductResponse>> search(@RequestParam String q, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.searchPaged(q, page, size));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }
}

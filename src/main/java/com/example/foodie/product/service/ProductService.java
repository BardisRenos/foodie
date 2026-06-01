package com.example.foodie.product.service;

import com.example.foodie.order.service.OrderQueryService;
import com.example.foodie.order.service.OrderService;
import com.example.foodie.product.dto.ProductDto;
import com.example.foodie.product.internal.Category;
import com.example.foodie.product.internal.Product;
import com.example.foodie.product.internal.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderQueryService orderQueryService;

    @CacheEvict(value = "products", allEntries = true)
    public ProductDto.ProductResponse create(ProductDto.CreateRequest request) {
        Product p = new Product();
        p.setName(request.name());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setUnit(request.unit());
        p.setStockQuantity(request.stockQuantity());
        p.setCategory(request.category());
        p.setImageUrl(request.imageUrl());
        p.setFarmerId(request.farmerId());
        return toResponse(productRepository.save(p));
    }

    public ProductDto.ProductResponse findById(String id) {
        return productRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

//    @Cacheable(value = "products", key = "'all'")
    public Page<ProductDto.ProductResponse> findAvailablePaged(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return productRepository.findByAvailableTrue(pageable).map(this::toResponse);
    }

    public List<ProductDto.ProductResponse> findByFarmer(String farmerId) {
        return productRepository.findByFarmerId(farmerId).stream().map(this::toResponse).toList();
    }

    public Page<ProductDto.ProductResponse> findByCategoryPaged(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        try {
            Category cat = Category.valueOf(category.toUpperCase().trim());
            return productRepository.findByAvailableTrueAndCategory(cat, pageable)
                    .map(this::toResponse);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category: " + category);
        }
    }

    public Page<ProductDto.ProductResponse> searchPaged(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String q = "%" + query.toLowerCase().trim() + "%";
        return productRepository.search(q, pageable).map(this::toResponse);
    }

    private ProductDto.ProductResponse toResponse(Product p) {
        return new ProductDto.ProductResponse(
            p.getId(), p.getName(), p.getDescription(), p.getPrice(),
            p.getUnit(), p.getStockQuantity(),
            p.getCategory() != null ? p.getCategory().name() : null,
            p.isAvailable(), p.getFarmerId(), p.getImageUrl()
        );
    }

    public ProductDto.ProductResponse update(String id, ProductDto.CreateRequest request) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        p.setName(request.name());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setUnit(request.unit());
        p.setStockQuantity(request.stockQuantity());
        p.setCategory(request.category());
        p.setImageUrl(request.imageUrl());
        return toResponse(productRepository.save(p));
    }

    @CacheEvict(value = "products", allEntries = true)
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found: " + id);
        }
        if (orderQueryService.hasActiveOrdersForProduct(id)) {
            throw new IllegalStateException(
                    "Cannot delete product — it has active orders. Mark it unavailable instead."
            );
        }
        productRepository.deleteById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductDto.ProductResponse toggleAvailability(String id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        p.setAvailable(!p.isAvailable());
        return toResponse(productRepository.save(p));
    }

    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Enum::name)
                .toList();
    }

    public ProductDto.ProductResponse validateAndGetProduct(String productId, String farmerId, int quantity) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (!p.getFarmerId().equals(farmerId)) {
            throw new RuntimeException("Product does not belong to this farmer");
        }
        if (!p.isAvailable()) {
            throw new RuntimeException("Product is not available: " + p.getName());
        }
        if (p.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for: " + p.getName() +
                    ". Available: " + p.getStockQuantity());
        }
        return toResponse(p);
    }
}

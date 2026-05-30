package com.example.foodie.product.service;

import com.example.foodie.product.dto.ProductDto;
import com.example.foodie.product.internal.Category;
import com.example.foodie.product.internal.Product;
import com.example.foodie.product.internal.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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

    @Cacheable(value = "products", key = "'all'")
    public List<ProductDto.ProductResponse> findAvailable() {
        return productRepository.findByAvailableTrue().stream().map(this::toResponse).toList();
    }

    public List<ProductDto.ProductResponse> findByFarmer(String farmerId) {
        return productRepository.findByFarmerId(farmerId).stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "products", key = "'category_' + #category")
    public List<ProductDto.ProductResponse> findByCategory(String category) {
        return productRepository.findByAvailableTrueAndCategory(Category.valueOf(category.toUpperCase()))
            .stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "products", key = "'search_' + #query")
    public List<ProductDto.ProductResponse> search(String query) {
        return productRepository.search(query).stream().map(this::toResponse).toList();
    }

    private ProductDto.ProductResponse toResponse(Product p) {
        return new ProductDto.ProductResponse(
            p.getId(), p.getName(), p.getDescription(), p.getPrice(),
            p.getUnit(), p.getStockQuantity(),
            p.getCategory() != null ? p.getCategory().name() : null,
            p.isAvailable(), p.getFarmerId(), p.getImageUrl()
        );
    }
}

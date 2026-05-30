package com.example.foodie.product.internal;

import com.example.foodie.product.internal.Category;
import com.example.foodie.product.internal.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByFarmerId(String farmerId);
    List<Product> findByCategory(Category category);
    List<Product> findByAvailableTrue();
    List<Product> findByAvailableTrueAndCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.available = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Product> search(@Param("q") String query);
}

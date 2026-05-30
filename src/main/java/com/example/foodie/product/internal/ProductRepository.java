package com.example.foodie.product.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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

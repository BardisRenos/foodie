package com.example.foodie.product.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByFarmerId(String farmerId);
    List<Product> findByCategory(Category category);
    Page<Product> findByAvailableTrue(Pageable pageable);
    Page<Product> findByAvailableTrueAndCategory(Category category, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.available = true AND " +
            "(LOWER(p.name) LIKE :q OR LOWER(p.description) LIKE :q)")
    Page<Product> search(@Param("q") String query, Pageable pageable);
}

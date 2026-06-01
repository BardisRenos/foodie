package com.example.foodie.review.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByProductId(String productId);
    List<Review> findByFarmerId(String farmerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
    Double avgRatingByProduct(@Param("productId") String productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.farmerId = :farmerId")
    Double avgRatingByFarmer(@Param("farmerId") String farmerId);

    boolean existsByUserIdAndProductIdAndOrderId(String userId, String productId, String orderId);
}

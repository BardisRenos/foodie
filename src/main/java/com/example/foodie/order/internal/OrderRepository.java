package com.example.foodie.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByFarmerId(String farmerId);
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Order> findByFarmerIdOrderByCreatedAtDesc(String farmerId);
}

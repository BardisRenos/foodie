package com.example.foodie.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByFarmerId(String farmerId);
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Order> findByFarmerIdOrderByCreatedAtDesc(String farmerId);
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, OrderStatus orderStatus);
    List<Order> findByFarmerIdAndStatusOrderByCreatedAtDesc(String farmerId, OrderStatus orderStatus);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i " +
            "WHERE i.productId = :productId AND o.status IN :statuses")
    boolean existsByItemsProductIdAndStatusIn(@Param("productId") String productId, @Param("statuses") List<OrderStatus> statuses);
}

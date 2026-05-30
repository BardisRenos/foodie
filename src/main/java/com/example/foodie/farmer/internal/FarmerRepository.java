package com.example.foodie.farmer.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, String> {
    Optional<Farmer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Farmer> findByVerifiedTrue();
    List<Farmer> findByFarmLocationContainingIgnoreCase(String location);
}

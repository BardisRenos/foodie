package com.example.foodie.farmer.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, String> {
    Optional<Farmer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Farmer> findByVerifiedTrue();
    List<Farmer> findByFarmLocationContainingIgnoreCase(String location);
}

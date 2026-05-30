package com.example.foodie.farmer.service;

import com.example.foodie.farmer.dto.FarmerDto;
import com.example.foodie.farmer.internal.Farmer;
import com.example.foodie.farmer.internal.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;


    public FarmerDto.FarmerResponse register(FarmerDto.RegisterRequest request) {
        if (farmerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }
        Farmer farmer = new Farmer();
        farmer.setFullName(request.fullName());
        farmer.setEmail(request.email());
        farmer.setPassword(passwordEncoder.encode(request.password()));
        farmer.setPhone(request.phone());
        farmer.setFarmName(request.farmName());
        farmer.setFarmLocation(request.farmLocation());
        farmer.setDescription(request.description());
        return toResponse(farmerRepository.save(farmer));
    }

    @Cacheable(value = "farmers", key = "#id")
    public FarmerDto.FarmerResponse findById(String id) {
        return farmerRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Farmer not found: " + id));
    }

    @Cacheable(value = "farmers", key = "'verified'")
    public List<FarmerDto.FarmerResponse> findVerified() {
        return farmerRepository.findByVerifiedTrue().stream().map(this::toResponse).toList();
    }

    public List<FarmerDto.FarmerResponse> findByLocation(String location) {
        return farmerRepository.findByFarmLocationContainingIgnoreCase(location)
            .stream().map(this::toResponse).toList();
    }

    private FarmerDto.FarmerResponse toResponse(Farmer f) {
        return new FarmerDto.FarmerResponse(
            f.getId(), f.getFullName(), f.getEmail(), f.getPhone(),
            f.getFarmName(), f.getFarmLocation(), f.getDescription(), f.isVerified()
        );
    }
}

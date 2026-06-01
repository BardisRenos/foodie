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
        farmer.setFarmerId(generateFarmerId());
        farmer.setFullName(request.fullName());
        farmer.setEmail(request.email());
        farmer.setPassword(passwordEncoder.encode(request.password()));
        farmer.setPhone(request.phone());
        farmer.setFarmName(request.farmName());
        farmer.setFarmLocation(request.farmLocation());
        farmer.setDescription(request.description());
        farmer.setVerified(false);
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

    public List<FarmerDto.FarmerResponse> findAll() {
        return farmerRepository.findAll().stream().map(this::toResponse).toList();
    }

    public FarmerDto.FarmerResponse verify(String id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found: " + id));
        farmer.setVerified(true);
        return toResponse(farmerRepository.save(farmer));
    }

    public FarmerDto.FarmerResponse unverify(String id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found: " + id));
        farmer.setVerified(false);
        return toResponse(farmerRepository.save(farmer));
    }

    public void delete(String id) {
        if (!farmerRepository.existsById(id)) {
            throw new RuntimeException("Farmer not found: " + id);
        }
        farmerRepository.deleteById(id);
    }

    public FarmerDto.FarmerResponse update(String id, FarmerDto.UpdateRequest request) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found: " + id));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            farmer.setFullName(request.fullName());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            farmer.setPhone(request.phone());
        }
        if (request.farmName() != null && !request.farmName().isBlank()) {
            farmer.setFarmName(request.farmName());
        }
        if (request.farmLocation() != null && !request.farmLocation().isBlank()) {
            farmer.setFarmLocation(request.farmLocation());
        }
        if (request.description() != null && !request.description().isBlank()) {
            farmer.setDescription(request.description());
        }
        return toResponse(farmerRepository.save(farmer));
    }

    private String generateFarmerId() {
        long count = farmerRepository.count() + 1;
        return String.format("FRM-%06d", count);
    }

    private FarmerDto.FarmerResponse toResponse(Farmer f) {
        return new FarmerDto.FarmerResponse(
            f.getId(), f.getFarmerId(), f.getFullName(), f.getEmail(), f.getPhone(),
            f.getFarmName(), f.getFarmLocation(), f.getDescription(), f.isVerified()
        );
    }
}

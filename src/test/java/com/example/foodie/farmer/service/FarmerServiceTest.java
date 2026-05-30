package com.example.foodie.farmer.service;

import com.example.foodie.farmer.dto.FarmerDto;
import com.example.foodie.farmer.internal.Farmer;
import com.example.foodie.farmer.internal.FarmerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmerServiceTest {

    @Mock
    private FarmerRepository farmerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FarmerService farmerService;

    private Farmer farmer;

    @BeforeEach
    void setUp() {
        farmer = new Farmer();
        farmer.setFullName("Nikos Farmer");
        farmer.setEmail("nikos@farm.com");
        farmer.setPassword("encoded_password");
        farmer.setPhone("6911111111");
        farmer.setFarmName("Nikos Fresh Farm");
        farmer.setFarmLocation("Crete, Greece");
        farmer.setDescription("Fresh organic vegetables");
        farmer.setVerified(true);
    }

    @Test
    void register_shouldSaveFarmer_whenEmailNotInUse() {
        FarmerDto.RegisterRequest request = new FarmerDto.RegisterRequest(
                "Nikos Farmer", "nikos@farm.com", "password123",
                "6911111111", "Nikos Fresh Farm", "Crete, Greece", "Fresh organic vegetables"
        );

        when(farmerRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(farmerRepository.save(any(Farmer.class))).thenReturn(farmer);

        FarmerDto.FarmerResponse response = farmerService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("nikos@farm.com");
        assertThat(response.farmName()).isEqualTo("Nikos Fresh Farm");
        verify(farmerRepository).save(any(Farmer.class));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyInUse() {
        FarmerDto.RegisterRequest request = new FarmerDto.RegisterRequest(
                "Nikos Farmer", "nikos@farm.com", "password123",
                "6911111111", "Nikos Fresh Farm", "Crete, Greece", "Fresh organic vegetables"
        );

        when(farmerRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> farmerService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(farmerRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnFarmer_whenExists() {
        when(farmerRepository.findById("f1")).thenReturn(Optional.of(farmer));

        FarmerDto.FarmerResponse response = farmerService.findById("f1");

        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo("Nikos Farmer");
        assertThat(response.verified()).isTrue();
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(farmerRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> farmerService.findById("unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Farmer not found");
    }

    @Test
    void findVerified_shouldReturnOnlyVerifiedFarmers() {
        when(farmerRepository.findByVerifiedTrue()).thenReturn(List.of(farmer));

        List<FarmerDto.FarmerResponse> result = farmerService.findVerified();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).verified()).isTrue();
    }

    @Test
    void findByLocation_shouldReturnFarmersMatchingLocation() {
        when(farmerRepository.findByFarmLocationContainingIgnoreCase("Crete"))
                .thenReturn(List.of(farmer));

        List<FarmerDto.FarmerResponse> result = farmerService.findByLocation("Crete");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).farmLocation()).isEqualTo("Crete, Greece");
    }

    @Test
    void findByLocation_shouldReturnEmpty_whenNoMatch() {
        when(farmerRepository.findByFarmLocationContainingIgnoreCase("Tokyo"))
                .thenReturn(List.of());

        List<FarmerDto.FarmerResponse> result = farmerService.findByLocation("Tokyo");

        assertThat(result).isEmpty();
    }
}
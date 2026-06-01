package com.example.foodie.farmer.controller;

import com.example.foodie.farmer.dto.FarmerDto;
import com.example.foodie.farmer.service.FarmerService;
import com.example.foodie.security.JwtAuthFilter;
import com.example.foodie.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FarmerController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        ))
class FarmerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FarmerService farmerService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private FarmerDto.FarmerResponse farmerResponse() {
        return new FarmerDto.FarmerResponse(
                "f1", "FRM-0001","Nikos Farmer", "nikos@farm.com",
                "6911111111", "Nikos Fresh Farm", "Crete, Greece",
                "Fresh organic vegetables", true
        );
    }

    @Test
    @WithMockUser(roles = "FARMER")
    void register_shouldReturn200_whenValidRequest() throws Exception {
        FarmerDto.RegisterRequest request = new FarmerDto.RegisterRequest(
                "Nikos Farmer", "nikos@farm.com", "password123",
                "6911111111", "Nikos Fresh Farm", "Crete, Greece", "Fresh organic vegetables"
        );

        when(farmerService.register(any())).thenReturn(farmerResponse());

        mockMvc.perform(post("/api/v1/auth/farmers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nikos@farm.com"))
                .andExpect(jsonPath("$.farmName").value("Nikos Fresh Farm"))
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    @WithMockUser
    void getFarmer_shouldReturn200_whenFarmerExists() throws Exception {
        when(farmerService.findById("f1")).thenReturn(farmerResponse());

        mockMvc.perform(get("/api/v1/farmers/f1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f1"))
                .andExpect(jsonPath("$.fullName").value("Nikos Farmer"))
                .andExpect(jsonPath("$.farmLocation").value("Crete, Greece"));
    }

    @Test
    @WithMockUser
    void listFarmers_shouldReturnVerifiedFarmers_whenNoLocationParam() throws Exception {
        when(farmerService.findVerified()).thenReturn(List.of(farmerResponse()));

        mockMvc.perform(get("/api/v1/farmers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].farmName").value("Nikos Fresh Farm"))
                .andExpect(jsonPath("$[0].verified").value(true));
    }

    @Test
    @WithMockUser
    void listFarmers_shouldReturnFilteredFarmers_whenLocationProvided() throws Exception {
        when(farmerService.findByLocation("Crete")).thenReturn(List.of(farmerResponse()));

        mockMvc.perform(get("/api/v1/farmers").param("location", "Crete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].farmLocation").value("Crete, Greece"));
    }

    @Test
    @WithMockUser
    void getFarmer_shouldReturn404_whenFarmerNotFound() throws Exception {
        when(farmerService.findById("unknown"))
                .thenThrow(new RuntimeException("Farmer not found: unknown"));

        mockMvc.perform(get("/api/v1/farmers/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Farmer not found: unknown"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
package com.example.foodie.user.controller;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.service.UserService;
import com.example.foodie.security.JwtAuthFilter;
import com.example.foodie.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        ))
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    private UserDto.UserResponse userResponse() {
        return new UserDto.UserResponse(
                "u1", "John Consumer", "john@example.com",
                "6901234567", "Athens, Greece", "CONSUMER"
        );
    }

    @Test
    @WithMockUser
    void register_shouldReturn200_whenValidRequest() throws Exception {
        UserDto.RegisterRequest request = new UserDto.RegisterRequest(
                "John Consumer", "john@example.com",
                "password123", "6901234567", "Athens, Greece"
        );

        when(userService.register(any())).thenReturn(userResponse());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Consumer"))
                .andExpect(jsonPath("$.role").value("CONSUMER"));
    }

    @Test
    @WithMockUser
    void register_shouldReturn409_whenEmailAlreadyInUse() throws Exception {
        UserDto.RegisterRequest request = new UserDto.RegisterRequest(
                "John Consumer", "john@example.com",
                "password123", "6901234567", "Athens, Greece"
        );

        when(userService.register(any()))
                .thenThrow(new IllegalArgumentException("Email already in use: john@example.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use: john@example.com"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser
    void register_shouldReturn400_whenInvalidRequest() throws Exception {
        UserDto.RegisterRequest request = new UserDto.RegisterRequest(
                "", "not-an-email", // invalid
                "password123", "6901234567", "Athens, Greece"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getUser_shouldReturn200_whenUserExists() throws Exception {
        when(userService.findById("u1")).thenReturn(userResponse());

        mockMvc.perform(get("/api/v1/auth/users/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.fullName").value("John Consumer"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser
    void getUser_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.findById("unknown"))
                .thenThrow(new RuntimeException("User not found: unknown"));

        mockMvc.perform(get("/api/v1/auth/users/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found: unknown"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
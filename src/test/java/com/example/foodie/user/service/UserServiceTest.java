package com.example.foodie.user.service;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.internal.User;
import com.example.foodie.user.internal.UserRepository;
import com.example.foodie.user.roletype.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("John Consumer");
        user.setEmail("john@example.com");
        user.setPassword("encoded_password");
        user.setPhone("6901234567");
        user.setAddress("Athens, Greece");
        user.setRole(Role.CONSUMER);
    }

    @Test
    void register_shouldSaveUser_whenEmailNotInUse() {
        UserDto.RegisterRequest request = new UserDto.RegisterRequest(
                "John Consumer", "john@example.com",
                "password123", "6901234567", "Athens, Greece"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto.UserResponse response = userService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.fullName()).isEqualTo("John Consumer");
        assertThat(response.role()).isEqualTo("CONSUMER");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyInUse() {
        UserDto.RegisterRequest request = new UserDto.RegisterRequest(
                "John Consumer", "john@example.com",
                "password123", "6901234567", "Athens, Greece"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        UserDto.UserResponse response = userService.findById("u1");

        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo("John Consumer");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.role()).isEqualTo("CONSUMER");
    }

    @Test
    void findById_shouldThrow_whenUserNotFound() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById("unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void register_shouldEncodePassword_beforeSaving() {
        UserDto.RegisterRequest request = new UserDto.RegisterRequest(
                "John Consumer", "john@example.com",
                "password123", "6901234567", "Athens, Greece"
        );

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(request);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(u ->
                u.getPassword().equals("encoded_password")
        ));
    }
}
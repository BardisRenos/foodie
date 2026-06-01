package com.example.foodie.user.service;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.internal.User;
import com.example.foodie.user.internal.UserRepository;
import com.example.foodie.user.roletype.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto.UserResponse register(UserDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }
        User user = new User();
        user.setUserId(generateUserId());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setAddress(request.address());
        return toResponse(userRepository.save(user));
    }

    public UserDto.UserResponse findById(String id) {
        return userRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public List<UserDto.UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDto.UserResponse update(String id, UserDto.UpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            user.setPhone(request.phone());
        }
        if (request.address() != null && !request.address().isBlank()) {
            user.setAddress(request.address());
        }
        return toResponse(userRepository.save(user));
    }

    private String generateUserId() {
        long count = userRepository.count() + 1;
        return String.format("USR-%06d", count); // USR-000001, USR-000002...
    }


    private UserDto.UserResponse toResponse(User u) {
        return new UserDto.UserResponse(
            u.getId(), u.getUserId(), u.getFullName(), u.getEmail(),
            u.getPhone(), u.getAddress(), u.getRole().name()
        );
    }

    public UserDto.UserResponse registerAdmin(UserDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setUserId(generateUserId());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ADMIN);  // ← only admins can create admins
        return toResponse(userRepository.save(user));
    }
}

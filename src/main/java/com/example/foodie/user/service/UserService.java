package com.example.foodie.user.service;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.internal.User;
import com.example.foodie.user.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    private UserDto.UserResponse toResponse(User u) {
        return new UserDto.UserResponse(
            u.getId(), u.getFullName(), u.getEmail(),
            u.getPhone(), u.getAddress(), u.getRole().name()
        );
    }
}

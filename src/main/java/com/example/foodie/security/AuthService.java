package com.example.foodie.security;

import com.example.foodie.farmer.internal.Farmer;
import com.example.foodie.farmer.internal.FarmerRepository;
import com.example.foodie.transaction.internal.ActorType;
import com.example.foodie.transaction.internal.TransactionStatus;
import com.example.foodie.transaction.internal.TransactionType;
import com.example.foodie.transaction.service.TransactionService;
import com.example.foodie.user.internal.User;
import com.example.foodie.user.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final JwtUtils jwtUtils;

    public AuthResponse loginUser(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getRole().name());
    }

    public AuthResponse loginFarmer(String email, String password) {
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        String token = jwtUtils.generateToken(farmer.getEmail(), "FARMER", farmer.getId());
        return new AuthResponse(token, farmer.getId(), farmer.getFullName(), "FARMER");
    }

    public String logout(String token) {
        // record transaction - get email from token to find actor
        String email = jwtUtils.getEmail(token);

        userRepository.findByEmail(email).ifPresent(user ->
                transactionService.record(
                        user.getId(), ActorType.USER,
                        TransactionType.LOGOUT, null,
                        "User logged out", TransactionStatus.SUCCESS
                )
        );
        // check farmers too
        farmerRepository.findByEmail(email).ifPresent(farmer ->
                transactionService.record(
                        farmer.getId(), ActorType.FARMER,
                        TransactionType.LOGOUT, null,
                        "Farmer logged out", TransactionStatus.SUCCESS
                )
        );
        return email;
    }
}
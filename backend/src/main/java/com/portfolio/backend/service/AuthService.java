package com.portfolio.backend.service;

import com.portfolio.backend.dto.request.LoginRequest;
import com.portfolio.backend.dto.request.SignupRequest;
import com.portfolio.backend.dto.response.AuthResponse;
import com.portfolio.backend.exception.ValidationException;
import com.portfolio.backend.model.User;
import com.portfolio.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponse signup(SignupRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            throw new ValidationException("Email is already registered");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User createdUser = userRepository.create(
                request.getName(),
                request.getEmail(),
                passwordHash
        );

        return toAuthResponse(createdUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new ValidationException("Invalid email or password");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new ValidationException("Invalid email or password");
        }

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                "dev-token-" + UUID.randomUUID()
        );
    }
}
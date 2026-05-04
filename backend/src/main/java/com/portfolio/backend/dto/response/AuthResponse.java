package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private UUID userId;
    private String name;
    private String email;
    private String token;
}
package com.portfolio.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}
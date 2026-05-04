package com.portfolio.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioHolding {
    private UUID id;
    private UUID portfolioId;
    private String ticker;
    private BigDecimal weight;
    private LocalDateTime createdAt;
}
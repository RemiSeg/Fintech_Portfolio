package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PortfolioSummaryResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
    private int numberOfHoldings;
}
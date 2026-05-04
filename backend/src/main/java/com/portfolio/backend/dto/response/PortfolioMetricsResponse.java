package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PortfolioMetricsResponse {
    private BigDecimal totalReturn;
    private BigDecimal volatility;
    private BigDecimal maxDrawdown;
}
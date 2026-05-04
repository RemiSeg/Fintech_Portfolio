package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PortfolioDetailResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
    private List<HoldingResponse> holdings;
    private PortfolioMetricsResponse metrics;
    private List<TimeSeriesPointResponse> performance;
}
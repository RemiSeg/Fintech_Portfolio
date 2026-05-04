package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CompareResponse {
    private Map<String, List<TimeSeriesPointResponse>> series;
    private Map<String, PortfolioMetricsResponse> metrics;
}
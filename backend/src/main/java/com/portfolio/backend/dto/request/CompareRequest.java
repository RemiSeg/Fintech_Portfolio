package com.portfolio.backend.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CompareRequest {

    private List<UUID> portfolioIds;

    private List<String> tickers;

    private String range;
}
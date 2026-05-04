package com.portfolio.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePortfolioRequest {

    private String name;

    private String description;

    @Valid
    @Size(min = 1, max = 10)
    private List<HoldingRequest> holdings;
}
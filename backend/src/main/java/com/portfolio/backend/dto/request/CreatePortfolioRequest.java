package com.portfolio.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreatePortfolioRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String name;

    private String description;

    @Valid
    @Size(min = 1, max = 10)
    private List<HoldingRequest> holdings;
}
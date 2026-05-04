package com.portfolio.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HoldingRequest {

    @NotBlank
    private String ticker;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal weight;
}
package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class HoldingResponse {
    private String ticker;
    private String companyName;
    private BigDecimal weight;
}
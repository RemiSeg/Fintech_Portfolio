package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StockPriceResponse {
    private String ticker;
    private LocalDate date;
    private BigDecimal closePrice;
}
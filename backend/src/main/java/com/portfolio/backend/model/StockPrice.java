package com.portfolio.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {
    private UUID id;
    private String ticker;
    private LocalDate priceDate;
    private BigDecimal closePrice;
}
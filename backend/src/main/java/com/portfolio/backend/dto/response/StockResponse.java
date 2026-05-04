package com.portfolio.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockResponse {
    private String ticker;
    private String companyName;
    private String sector;
    private String exchange;
}
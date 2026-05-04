package com.portfolio.backend.controller;

import com.portfolio.backend.dto.response.StockPriceResponse;
import com.portfolio.backend.dto.response.StockResponse;
import com.portfolio.backend.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockResponse> getSupportedStocks() {
        return stockService.getSupportedStocks();
    }

    @GetMapping("/{ticker}")
    public StockResponse getStock(@PathVariable String ticker) {
        return stockService.getStock(ticker);
    }

    @GetMapping("/{ticker}/history")
    public List<StockPriceResponse> getStockHistory(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "1Y") String range
    ) {
        return stockService.getStockHistory(ticker, range);
    }
}
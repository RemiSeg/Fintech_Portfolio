package com.portfolio.backend.controller;

import com.portfolio.backend.model.Stock;
import com.portfolio.backend.repository.StockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HealthController {

    private final StockRepository stockRepository;

    public HealthController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @GetMapping("/health")
    public String health() {
        return "Backend is running";
    }

    @GetMapping("/health/supabase")
    public List<Stock> supabaseHealth() {
        return stockRepository.findAll();
    }
}
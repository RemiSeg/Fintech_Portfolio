package com.portfolio.backend.controller;

import com.portfolio.backend.dto.request.CreatePortfolioRequest;
import com.portfolio.backend.dto.request.UpdatePortfolioRequest;
import com.portfolio.backend.dto.response.PortfolioDetailResponse;
import com.portfolio.backend.dto.response.PortfolioSummaryResponse;
import com.portfolio.backend.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/portfolios")
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/default")
    public List<PortfolioSummaryResponse> getDefaultPortfolios() {
        return portfolioService.getDefaultPortfolios();
    }

    @GetMapping("/user/{userId}")
    public List<PortfolioSummaryResponse> getUserPortfolios(@PathVariable UUID userId) {
        return portfolioService.getUserPortfolios(userId);
    }

    @GetMapping("/{portfolioId}")
    public PortfolioDetailResponse getPortfolioDetail(@PathVariable UUID portfolioId) {
        return portfolioService.getPortfolioDetail(portfolioId);
    }

    @PostMapping
    public PortfolioDetailResponse createPortfolio(@Valid @RequestBody CreatePortfolioRequest request) {
        return portfolioService.createPortfolio(request);
    }

    @PutMapping("/{portfolioId}")
    public PortfolioDetailResponse updatePortfolio(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody UpdatePortfolioRequest request
    ) {
        return portfolioService.updatePortfolio(portfolioId, request);
    }

    @DeleteMapping("/{portfolioId}")
    public void deletePortfolio(@PathVariable UUID portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
    }
}
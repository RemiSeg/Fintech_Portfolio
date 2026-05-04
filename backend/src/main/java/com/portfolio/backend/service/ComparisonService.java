package com.portfolio.backend.service;

import com.portfolio.backend.client.YahooFinanceClient;
import com.portfolio.backend.dto.request.CompareRequest;
import com.portfolio.backend.dto.response.CompareResponse;
import com.portfolio.backend.dto.response.PortfolioMetricsResponse;
import com.portfolio.backend.dto.response.StockPriceResponse;
import com.portfolio.backend.dto.response.TimeSeriesPointResponse;
import com.portfolio.backend.exception.NotFoundException;
import com.portfolio.backend.model.Portfolio;
import com.portfolio.backend.model.PortfolioHolding;
import com.portfolio.backend.repository.HoldingRepository;
import com.portfolio.backend.repository.PortfolioRepository;
import com.portfolio.backend.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ComparisonService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final YahooFinanceClient yahooFinanceClient;
    private final PortfolioService portfolioService;
    private final MetricsService metricsService;

    public ComparisonService(
            PortfolioRepository portfolioRepository,
            HoldingRepository holdingRepository,
            StockRepository stockRepository,
            YahooFinanceClient yahooFinanceClient,
            PortfolioService portfolioService,
            MetricsService metricsService
    ) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
        this.yahooFinanceClient = yahooFinanceClient;
        this.portfolioService = portfolioService;
        this.metricsService = metricsService;
    }

    public CompareResponse compare(CompareRequest request) {
        String range = request.getRange() == null ? "1Y" : request.getRange();

        Map<String, List<TimeSeriesPointResponse>> series = new HashMap<>();
        Map<String, PortfolioMetricsResponse> metrics = new HashMap<>();

        if (request.getPortfolioIds() != null) {
            for (UUID portfolioId : request.getPortfolioIds()) {
                Portfolio portfolio = portfolioRepository.findById(portfolioId);

                if (portfolio == null) {
                    throw new NotFoundException("Portfolio not found: " + portfolioId);
                }

                List<PortfolioHolding> holdings = holdingRepository.findByPortfolioId(portfolioId);

                List<TimeSeriesPointResponse> portfolioSeries =
                        portfolioService.buildPortfolioPerformance(holdings, range);

                series.put(portfolio.getName(), portfolioSeries);
                metrics.put(portfolio.getName(), metricsService.calculateMetrics(portfolioSeries));
            }
        }

        if (request.getTickers() != null) {
            for (String ticker : request.getTickers()) {
                String normalizedTicker = ticker.toUpperCase();

                if (!stockRepository.existsByTicker(normalizedTicker)) {
                    throw new NotFoundException("Stock not found: " + normalizedTicker);
                }

                List<StockPriceResponse> prices =
                        yahooFinanceClient.getHistoricalPrices(normalizedTicker, range);

                List<TimeSeriesPointResponse> stockSeries =
                        metricsService.normalizeStockPrices(prices);

                series.put(normalizedTicker, stockSeries);
                metrics.put(normalizedTicker, metricsService.calculateMetrics(stockSeries));
            }
        }

        return new CompareResponse(series, metrics);
    }
}
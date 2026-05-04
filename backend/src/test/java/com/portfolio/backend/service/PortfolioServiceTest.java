package com.portfolio.backend.service;

import com.portfolio.backend.client.YahooFinanceClient;
import com.portfolio.backend.dto.request.CreatePortfolioRequest;
import com.portfolio.backend.dto.request.HoldingRequest;
import com.portfolio.backend.exception.ValidationException;
import com.portfolio.backend.repository.HoldingRepository;
import com.portfolio.backend.repository.PortfolioRepository;
import com.portfolio.backend.repository.StockRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    private final PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
    private final HoldingRepository holdingRepository = mock(HoldingRepository.class);
    private final StockRepository stockRepository = mock(StockRepository.class);
    private final YahooFinanceClient yahooFinanceClient = mock(YahooFinanceClient.class);
    private final MetricsService metricsService = new MetricsService();

    private final PortfolioService portfolioService = new PortfolioService(
            portfolioRepository,
            holdingRepository,
            stockRepository,
            yahooFinanceClient,
            metricsService
    );

    @Test
    void createPortfolioRejectsMoreThanThreeUserPortfolios() {
        UUID userId = UUID.randomUUID();

        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setUserId(userId);
        request.setName("Too Many");
        request.setHoldings(List.of(validHolding("AAPL", 100)));

        when(portfolioRepository.countCustomPortfoliosByUserId(userId)).thenReturn(3);

        assertThrows(ValidationException.class, () -> portfolioService.createPortfolio(request));
    }

    @Test
    void createPortfolioRejectsWeightsNotSummingToOneHundred() {
        UUID userId = UUID.randomUUID();

        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setUserId(userId);
        request.setName("Bad Weights");
        request.setHoldings(List.of(
                validHolding("AAPL", 60),
                validHolding("MSFT", 20)
        ));

        when(portfolioRepository.countCustomPortfoliosByUserId(userId)).thenReturn(0);
        when(stockRepository.existsByTicker("AAPL")).thenReturn(true);
        when(stockRepository.existsByTicker("MSFT")).thenReturn(true);

        assertThrows(ValidationException.class, () -> portfolioService.createPortfolio(request));
    }

    @Test
    void createPortfolioRejectsUnsupportedTicker() {
        UUID userId = UUID.randomUUID();

        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setUserId(userId);
        request.setName("Invalid Ticker");
        request.setHoldings(List.of(validHolding("FAKE", 100)));

        when(portfolioRepository.countCustomPortfoliosByUserId(userId)).thenReturn(0);
        when(stockRepository.existsByTicker("FAKE")).thenReturn(false);

        assertThrows(ValidationException.class, () -> portfolioService.createPortfolio(request));
    }

    private HoldingRequest validHolding(String ticker, int weight) {
        HoldingRequest holding = new HoldingRequest();
        holding.setTicker(ticker);
        holding.setWeight(BigDecimal.valueOf(weight));
        return holding;
    }
}
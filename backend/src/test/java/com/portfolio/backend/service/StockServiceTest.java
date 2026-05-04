package com.portfolio.backend.service;

import com.portfolio.backend.client.YahooFinanceClient;
import com.portfolio.backend.dto.response.StockResponse;
import com.portfolio.backend.exception.NotFoundException;
import com.portfolio.backend.model.Stock;
import com.portfolio.backend.repository.StockRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StockServiceTest {

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final YahooFinanceClient yahooFinanceClient = mock(YahooFinanceClient.class);

    private final StockService stockService = new StockService(
            stockRepository,
            yahooFinanceClient
    );

    @Test
    void getSupportedStocksReturnsStockResponses() {
        when(stockRepository.findAll()).thenReturn(List.of(
                new Stock("AAPL", "Apple Inc.", "Technology", "NASDAQ")
        ));

        List<StockResponse> result = stockService.getSupportedStocks();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTicker()).isEqualTo("AAPL");
        assertThat(result.getFirst().getCompanyName()).isEqualTo("Apple Inc.");
    }

    @Test
    void getStockThrowsWhenTickerNotFound() {
        when(stockRepository.findByTicker("FAKE")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.getStock("FAKE"));
    }
}
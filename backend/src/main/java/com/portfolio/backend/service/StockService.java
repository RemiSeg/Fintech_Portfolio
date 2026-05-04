package com.portfolio.backend.service;

import com.portfolio.backend.client.YahooFinanceClient;
import com.portfolio.backend.dto.response.StockPriceResponse;
import com.portfolio.backend.dto.response.StockResponse;
import com.portfolio.backend.exception.NotFoundException;
import com.portfolio.backend.model.Stock;
import com.portfolio.backend.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final YahooFinanceClient yahooFinanceClient;

    public StockService(
            StockRepository stockRepository,
            YahooFinanceClient yahooFinanceClient
    ) {
        this.stockRepository = stockRepository;
        this.yahooFinanceClient = yahooFinanceClient;
    }

    public List<StockResponse> getSupportedStocks() {
        return stockRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StockResponse getStock(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker);

        if (stock == null) {
            throw new NotFoundException("Stock not found: " + ticker);
        }

        return toResponse(stock);
    }

    public List<StockPriceResponse> getStockHistory(String ticker, String range) {
        Stock stock = stockRepository.findByTicker(ticker);

        if (stock == null) {
            throw new NotFoundException("Stock not found: " + ticker);
        }

        return yahooFinanceClient.getHistoricalPrices(ticker, range);
    }

    public boolean isSupportedTicker(String ticker) {
        return stockRepository.existsByTicker(ticker);
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getTicker(),
                stock.getCompanyName(),
                stock.getSector(),
                stock.getExchange()
        );
    }
}
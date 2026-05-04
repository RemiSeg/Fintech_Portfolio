package com.portfolio.backend.service;

import com.portfolio.backend.dto.response.PortfolioMetricsResponse;
import com.portfolio.backend.dto.response.StockPriceResponse;
import com.portfolio.backend.dto.response.TimeSeriesPointResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MetricsService {

    public PortfolioMetricsResponse calculateMetrics(List<TimeSeriesPointResponse> series) {
        if (series == null || series.size() < 2) {
            return emptyMetrics();
        }

        BigDecimal first = series.getFirst().getValue();
        BigDecimal last = series.getLast().getValue();

        BigDecimal totalReturn = last.subtract(first)
                .divide(first, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        BigDecimal volatility = calculateVolatility(series);
        BigDecimal maxDrawdown = calculateMaxDrawdown(series);

        return new PortfolioMetricsResponse(
                totalReturn,
                volatility,
                maxDrawdown
        );
    }

    public List<TimeSeriesPointResponse> normalizeStockPrices(List<StockPriceResponse> prices) {
        if (prices == null || prices.isEmpty()) {
            return List.of();
        }

        BigDecimal firstPrice = prices.getFirst().getClosePrice();

        if (firstPrice.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        return prices.stream()
                .map(price -> new TimeSeriesPointResponse(
                        price.getDate(),
                        price.getClosePrice()
                                .divide(firstPrice, 8, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                ))
                .toList();
    }

    public List<TimeSeriesPointResponse> normalizeSeries(List<TimeSeriesPointResponse> series) {
        if (series == null || series.isEmpty()) {
            return List.of();
        }

        BigDecimal firstValue = series.getFirst().getValue();

        if (firstValue.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        return series.stream()
                .map(point -> new TimeSeriesPointResponse(
                        point.getDate(),
                        point.getValue()
                                .divide(firstValue, 8, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                ))
                .toList();
    }

    public PortfolioMetricsResponse emptyMetrics() {
        return new PortfolioMetricsResponse(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private BigDecimal calculateVolatility(List<TimeSeriesPointResponse> series) {
        if (series.size() < 2) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> returns = new java.util.ArrayList<>();

        for (int i = 1; i < series.size(); i++) {
            BigDecimal previous = series.get(i - 1).getValue();
            BigDecimal current = series.get(i).getValue();

            if (previous.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal dailyReturn = current.subtract(previous)
                    .divide(previous, 8, RoundingMode.HALF_UP);

            returns.add(dailyReturn);
        }

        if (returns.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double mean = returns.stream()
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);

        double variance = returns.stream()
                .mapToDouble(value -> Math.pow(value.doubleValue() - mean, 2))
                .average()
                .orElse(0.0);

        double dailyVolatility = Math.sqrt(variance);
        double annualizedVolatility = dailyVolatility * Math.sqrt(252) * 100;

        return BigDecimal.valueOf(annualizedVolatility)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMaxDrawdown(List<TimeSeriesPointResponse> series) {
        BigDecimal peak = series.getFirst().getValue();
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        for (TimeSeriesPointResponse point : series) {
            BigDecimal current = point.getValue();

            if (current.compareTo(peak) > 0) {
                peak = current;
            }

            if (peak.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal drawdown = current.subtract(peak)
                    .divide(peak, 8, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            if (drawdown.compareTo(maxDrawdown) < 0) {
                maxDrawdown = drawdown;
            }
        }

        return maxDrawdown.setScale(4, RoundingMode.HALF_UP);
    }
}
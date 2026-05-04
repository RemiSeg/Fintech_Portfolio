package com.portfolio.backend.service;

import com.portfolio.backend.dto.response.PortfolioMetricsResponse;
import com.portfolio.backend.dto.response.TimeSeriesPointResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsServiceTest {

    private final MetricsService metricsService = new MetricsService();

    @Test
    void calculateMetricsComputesTotalReturn() {
        List<TimeSeriesPointResponse> series = List.of(
                new TimeSeriesPointResponse(LocalDate.of(2026, 1, 1), BigDecimal.valueOf(100)),
                new TimeSeriesPointResponse(LocalDate.of(2026, 1, 2), BigDecimal.valueOf(110))
        );

        PortfolioMetricsResponse metrics = metricsService.calculateMetrics(series);

        assertThat(metrics.getTotalReturn()).isEqualByComparingTo(BigDecimal.valueOf(10.00000000));
    }

    @Test
    void normalizeSeriesStartsAtOneHundred() {
        List<TimeSeriesPointResponse> series = List.of(
                new TimeSeriesPointResponse(LocalDate.of(2026, 1, 1), BigDecimal.valueOf(50)),
                new TimeSeriesPointResponse(LocalDate.of(2026, 1, 2), BigDecimal.valueOf(75))
        );

        List<TimeSeriesPointResponse> normalized = metricsService.normalizeSeries(series);

        assertThat(normalized).hasSize(2);
        assertThat(normalized.getFirst().getValue()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(normalized.getLast().getValue()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void emptySeriesReturnsEmptyMetrics() {
        PortfolioMetricsResponse metrics = metricsService.calculateMetrics(List.of());

        assertThat(metrics.getTotalReturn()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(metrics.getVolatility()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(metrics.getMaxDrawdown()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
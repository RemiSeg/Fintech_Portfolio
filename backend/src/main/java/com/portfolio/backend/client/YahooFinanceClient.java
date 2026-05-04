package com.portfolio.backend.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.backend.dto.response.StockPriceResponse;
import com.portfolio.backend.exception.ExternalApiException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public YahooFinanceClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<StockPriceResponse> getHistoricalPrices(String ticker, String range) {
        try {
            String normalizedTicker = ticker.toUpperCase();
            String intervalRange = normalizeRange(range);

            String url = "https://query1.finance.yahoo.com/v8/finance/chart/"
                    + normalizedTicker
                    + "?range="
                    + intervalRange
                    + "&interval=1d";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ExternalApiException(
                        "Yahoo Finance request failed for ticker: "
                                + normalizedTicker
                                + " with status "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            return parseHistoricalPrices(normalizedTicker, response.body());
        } catch (ExternalApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ExternalApiException("Could not fetch historical prices for ticker: " + ticker, exception);
        }
    }

    private String normalizeRange(String range) {
        if (range == null || range.isBlank()) {
            return "1y";
        }

        return switch (range.toUpperCase()) {
            case "1M" -> "1mo";
            case "3M" -> "3mo";
            case "6M" -> "6mo";
            case "1Y" -> "1y";
            case "ALL" -> "5y";
            default -> "1y";
        };
    }

    private List<StockPriceResponse> parseHistoricalPrices(String ticker, String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode result = root.path("chart").path("result");

        if (!result.isArray() || result.isEmpty()) {
            return List.of();
        }

        JsonNode firstResult = result.get(0);
        JsonNode timestamps = firstResult.path("timestamp");
        JsonNode closePrices = firstResult
                .path("indicators")
                .path("quote")
                .get(0)
                .path("close");

        List<StockPriceResponse> prices = new ArrayList<>();

        for (int i = 0; i < timestamps.size(); i++) {
            JsonNode closeNode = closePrices.get(i);

            if (closeNode == null || closeNode.isNull()) {
                continue;
            }

            long epochSeconds = timestamps.get(i).asLong();
            LocalDate date = Instant.ofEpochSecond(epochSeconds)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();

            BigDecimal closePrice = BigDecimal.valueOf(closeNode.asDouble());

            prices.add(new StockPriceResponse(ticker, date, closePrice));
        }

        return prices;
    }
}
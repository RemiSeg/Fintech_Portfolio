package com.portfolio.backend.service;

import com.portfolio.backend.client.YahooFinanceClient;
import com.portfolio.backend.dto.request.CreatePortfolioRequest;
import com.portfolio.backend.dto.request.HoldingRequest;
import com.portfolio.backend.dto.request.UpdatePortfolioRequest;
import com.portfolio.backend.dto.response.HoldingResponse;
import com.portfolio.backend.dto.response.PortfolioDetailResponse;
import com.portfolio.backend.dto.response.PortfolioSummaryResponse;
import com.portfolio.backend.dto.response.StockPriceResponse;
import com.portfolio.backend.dto.response.TimeSeriesPointResponse;
import com.portfolio.backend.exception.NotFoundException;
import com.portfolio.backend.exception.ValidationException;
import com.portfolio.backend.model.Portfolio;
import com.portfolio.backend.model.PortfolioHolding;
import com.portfolio.backend.model.Stock;
import com.portfolio.backend.repository.HoldingRepository;
import com.portfolio.backend.repository.PortfolioRepository;
import com.portfolio.backend.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class PortfolioService {

    private static final int MAX_PORTFOLIOS_PER_USER = 3;
    private static final int MAX_HOLDINGS_PER_PORTFOLIO = 10;
    private static final BigDecimal REQUIRED_WEIGHT_SUM = BigDecimal.valueOf(100);

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final YahooFinanceClient yahooFinanceClient;
    private final MetricsService metricsService;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            HoldingRepository holdingRepository,
            StockRepository stockRepository,
            YahooFinanceClient yahooFinanceClient,
            MetricsService metricsService
    ) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
        this.yahooFinanceClient = yahooFinanceClient;
        this.metricsService = metricsService;
    }

    public List<PortfolioSummaryResponse> getDefaultPortfolios() {
        return portfolioRepository.findDefaultPortfolios()
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public List<PortfolioSummaryResponse> getUserPortfolios(UUID userId) {
        return portfolioRepository.findByUserId(userId)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public PortfolioDetailResponse getPortfolioDetail(UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId);

        if (portfolio == null) {
            throw new NotFoundException("Portfolio not found: " + portfolioId);
        }

        List<PortfolioHolding> holdings = holdingRepository.findByPortfolioId(portfolioId);

        List<HoldingResponse> holdingResponses = holdings.stream()
                .map(this::toHoldingResponse)
                .toList();

        List<TimeSeriesPointResponse> performance = buildPortfolioPerformance(holdings, "1Y");

        return new PortfolioDetailResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getDescription(),
                portfolio.getIsDefault(),
                holdingResponses,
                metricsService.calculateMetrics(performance),
                performance
        );
    }

    public PortfolioDetailResponse createPortfolio(CreatePortfolioRequest request) {
        validateCreateRequest(request);

        Portfolio createdPortfolio = portfolioRepository.create(
                request.getUserId(),
                request.getName(),
                request.getDescription()
        );

        holdingRepository.replaceHoldings(createdPortfolio.getId(), request.getHoldings());

        return getPortfolioDetail(createdPortfolio.getId());
    }

    public PortfolioDetailResponse updatePortfolio(UUID portfolioId, UpdatePortfolioRequest request) {
        Portfolio existing = portfolioRepository.findById(portfolioId);

        if (existing == null) {
            throw new NotFoundException("Portfolio not found: " + portfolioId);
        }

        if (Boolean.TRUE.equals(existing.getIsDefault())) {
            throw new ValidationException("Default portfolios cannot be edited");
        }

        if (request.getHoldings() != null) {
            validateHoldings(request.getHoldings());
        }

        String updatedName = request.getName() == null ? existing.getName() : request.getName();
        String updatedDescription = request.getDescription() == null ? existing.getDescription() : request.getDescription();

        portfolioRepository.update(portfolioId, updatedName, updatedDescription);

        if (request.getHoldings() != null) {
            holdingRepository.replaceHoldings(portfolioId, request.getHoldings());
        }

        return getPortfolioDetail(portfolioId);
    }

    public void deletePortfolio(UUID portfolioId) {
        Portfolio existing = portfolioRepository.findById(portfolioId);

        if (existing == null) {
            throw new NotFoundException("Portfolio not found: " + portfolioId);
        }

        if (Boolean.TRUE.equals(existing.getIsDefault())) {
            throw new ValidationException("Default portfolios cannot be deleted");
        }

        portfolioRepository.delete(portfolioId);
    }

    public List<TimeSeriesPointResponse> buildPortfolioPerformance(
            List<PortfolioHolding> holdings,
            String range
    ) {
        if (holdings == null || holdings.isEmpty()) {
            return List.of();
        }

        Map<LocalDate, BigDecimal> weightedValuesByDate = new TreeMap<>();

        for (PortfolioHolding holding : holdings) {
            List<StockPriceResponse> prices = yahooFinanceClient.getHistoricalPrices(
                    holding.getTicker(),
                    range
            );

            if (prices.isEmpty()) {
                continue;
            }

            BigDecimal firstPrice = prices.getFirst().getClosePrice();

            if (firstPrice.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal weight = holding.getWeight()
                    .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);

            for (StockPriceResponse price : prices) {
                BigDecimal normalizedValue = price.getClosePrice()
                        .divide(firstPrice, 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .multiply(weight);

                weightedValuesByDate.merge(
                        price.getDate(),
                        normalizedValue,
                        BigDecimal::add
                );
            }
        }

        return weightedValuesByDate.entrySet()
                .stream()
                .map(entry -> new TimeSeriesPointResponse(
                        entry.getKey(),
                        entry.getValue().setScale(4, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    private void validateCreateRequest(CreatePortfolioRequest request) {
        int existingPortfolios = portfolioRepository.countCustomPortfoliosByUserId(request.getUserId());

        if (existingPortfolios >= MAX_PORTFOLIOS_PER_USER) {
            throw new ValidationException("User cannot create more than 3 portfolios");
        }

        validateHoldings(request.getHoldings());
    }

    private void validateHoldings(List<HoldingRequest> holdings) {
        if (holdings == null || holdings.isEmpty()) {
            throw new ValidationException("Portfolio must contain at least one holding");
        }

        if (holdings.size() > MAX_HOLDINGS_PER_PORTFOLIO) {
            throw new ValidationException("Portfolio cannot contain more than 10 holdings");
        }

        BigDecimal totalWeight = BigDecimal.ZERO;

        for (HoldingRequest holding : holdings) {
            String ticker = holding.getTicker().toUpperCase();

            if (!stockRepository.existsByTicker(ticker)) {
                throw new ValidationException("Unsupported ticker: " + ticker);
            }

            totalWeight = totalWeight.add(holding.getWeight());
        }

        if (totalWeight.compareTo(REQUIRED_WEIGHT_SUM) != 0) {
            throw new ValidationException("Portfolio weights must sum to 100");
        }
    }

    private PortfolioSummaryResponse toSummaryResponse(Portfolio portfolio) {
        int numberOfHoldings = holdingRepository.findByPortfolioId(portfolio.getId()).size();

        return new PortfolioSummaryResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getDescription(),
                portfolio.getIsDefault(),
                numberOfHoldings
        );
    }

    private HoldingResponse toHoldingResponse(PortfolioHolding holding) {
        Stock stock = stockRepository.findByTicker(holding.getTicker());

        String companyName = stock == null ? holding.getTicker() : stock.getCompanyName();

        return new HoldingResponse(
                holding.getTicker(),
                companyName,
                holding.getWeight()
        );
    }
}
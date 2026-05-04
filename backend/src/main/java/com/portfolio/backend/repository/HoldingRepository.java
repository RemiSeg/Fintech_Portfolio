package com.portfolio.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.portfolio.backend.client.SupabaseClient;
import com.portfolio.backend.dto.request.HoldingRequest;
import com.portfolio.backend.model.PortfolioHolding;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class HoldingRepository {

    private final SupabaseClient supabaseClient;

    public HoldingRepository(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    public List<PortfolioHolding> findByPortfolioId(UUID portfolioId) {
        return supabaseClient.getList(
                "portfolio_holdings?portfolio_id=eq." + portfolioId + "&select=*&order=created_at.asc",
                new TypeReference<List<PortfolioHolding>>() {}
        );
    }

    public PortfolioHolding create(UUID portfolioId, String ticker, Object weight) {
        Map<String, Object> body = Map.of(
                "portfolio_id", portfolioId,
                "ticker", ticker.toUpperCase(),
                "weight", weight
        );

        return supabaseClient.post(
                "portfolio_holdings",
                body,
                new TypeReference<List<PortfolioHolding>>() {}
        );
    }

    public void deleteByPortfolioId(UUID portfolioId) {
        supabaseClient.delete("portfolio_holdings?portfolio_id=eq." + portfolioId);
    }

    public List<PortfolioHolding> replaceHoldings(UUID portfolioId, List<HoldingRequest> holdings) {
        deleteByPortfolioId(portfolioId);

        List<PortfolioHolding> createdHoldings = new ArrayList<>();

        for (HoldingRequest holding : holdings) {
            PortfolioHolding created = create(
                    portfolioId,
                    holding.getTicker(),
                    holding.getWeight()
            );
            createdHoldings.add(created);
        }

        return createdHoldings;
    }
}
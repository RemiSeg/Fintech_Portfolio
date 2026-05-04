package com.portfolio.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.portfolio.backend.client.SupabaseClient;
import com.portfolio.backend.model.Portfolio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class PortfolioRepository {

    private final SupabaseClient supabaseClient;

    public PortfolioRepository(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    public List<Portfolio> findDefaultPortfolios() {
        return supabaseClient.getList(
                "portfolios?is_default=eq.true&select=*&order=created_at.asc",
                new TypeReference<List<Portfolio>>() {}
        );
    }

    public List<Portfolio> findByUserId(UUID userId) {
        return supabaseClient.getList(
                "portfolios?user_id=eq." + userId + "&is_default=eq.false&select=*&order=created_at.asc",
                new TypeReference<List<Portfolio>>() {}
        );
    }

    public Portfolio findById(UUID portfolioId) {
        return supabaseClient.getOne(
                "portfolios?id=eq." + portfolioId + "&select=*",
                new TypeReference<List<Portfolio>>() {}
        );
    }

    public Portfolio create(UUID userId, String name, String description) {
        Map<String, Object> body = Map.of(
                "user_id", userId,
                "name", name,
                "description", description == null ? "" : description,
                "is_default", false
        );

        return supabaseClient.post(
                "portfolios",
                body,
                new TypeReference<List<Portfolio>>() {}
        );
    }

    public Portfolio update(UUID portfolioId, String name, String description) {
        Map<String, Object> body = Map.of(
                "name", name,
                "description", description == null ? "" : description
        );

        return supabaseClient.patch(
                "portfolios?id=eq." + portfolioId,
                body,
                new TypeReference<List<Portfolio>>() {}
        );
    }

    public void delete(UUID portfolioId) {
        supabaseClient.delete("portfolios?id=eq." + portfolioId);
    }

    public int countCustomPortfoliosByUserId(UUID userId) {
        return findByUserId(userId).size();
    }
}
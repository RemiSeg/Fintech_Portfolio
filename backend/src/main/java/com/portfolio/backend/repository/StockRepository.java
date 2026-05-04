package com.portfolio.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.portfolio.backend.client.SupabaseClient;
import com.portfolio.backend.model.Stock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockRepository {

    private final SupabaseClient supabaseClient;

    public StockRepository(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    public List<Stock> findAll() {
        return supabaseClient.getList(
                "stocks?select=*&order=ticker.asc",
                new TypeReference<List<Stock>>() {}
        );
    }

    public Stock findByTicker(String ticker) {
        return supabaseClient.getOne(
                "stocks?ticker=eq." + ticker.toUpperCase() + "&select=*",
                new TypeReference<List<Stock>>() {}
        );
    }

    public boolean existsByTicker(String ticker) {
        return findByTicker(ticker) != null;
    }
}
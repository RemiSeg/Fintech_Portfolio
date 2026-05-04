package com.portfolio.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.portfolio.backend.client.SupabaseClient;
import com.portfolio.backend.model.StockPrice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockPriceRepository {

    private final SupabaseClient supabaseClient;

    public StockPriceRepository(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    public List<StockPrice> findByTicker(String ticker) {
        return supabaseClient.getList(
                "stock_prices?ticker=eq." + ticker.toUpperCase() + "&select=*&order=price_date.asc",
                new TypeReference<List<StockPrice>>() {}
        );
    }
}
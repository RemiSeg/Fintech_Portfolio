package com.portfolio.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseConfig {

    private final String url;
    private final String key;

    public SupabaseConfig() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        this.url = dotenv.get("SUPABASE_URL");
        this.key = dotenv.get("SUPABASE_KEY");

        System.out.println("SUPABASE URL = " + url);
        System.out.println("SUPABASE KEY LOADED = " + (key != null));
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }
}
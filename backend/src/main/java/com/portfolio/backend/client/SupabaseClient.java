package com.portfolio.backend.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portfolio.backend.config.SupabaseConfig;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class SupabaseClient {

    private final SupabaseConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SupabaseClient(SupabaseConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule());
    }

    public <T> List<T> getList(String path, TypeReference<List<T>> typeReference) {
        try {
            HttpRequest request = baseRequest(path)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);

            return objectMapper.readValue(response.body(), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Supabase GET list request failed: " + path, e);
        }
    }

    public <T> T getOne(String path, TypeReference<List<T>> typeReference) {
        List<T> results = getList(path, typeReference);
        return results.isEmpty() ? null : results.getFirst();
    }

    public <T> T post(String path, Object body, TypeReference<List<T>> typeReference) {
        try {
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = baseRequest(path)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);

            List<T> results = objectMapper.readValue(response.body(), typeReference);
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Supabase POST request failed: " + path, e);
        }
    }

    public <T> T patch(String path, Object body, TypeReference<List<T>> typeReference) {
        try {
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = baseRequest(path)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);

            List<T> results = objectMapper.readValue(response.body(), typeReference);
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Supabase PATCH request failed: " + path, e);
        }
    }

    public void delete(String path) {
        try {
            HttpRequest request = baseRequest(path)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Supabase DELETE request failed: " + path, e);
        }
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(config.getUrl() + "/rest/v1/" + path))
                .header("apikey", config.getKey())
                .header("Authorization", "Bearer " + config.getKey());
    }

    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException(
                    "Supabase request failed with status "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }
    }
}
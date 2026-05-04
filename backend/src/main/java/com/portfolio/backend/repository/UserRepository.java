package com.portfolio.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.portfolio.backend.client.SupabaseClient;
import com.portfolio.backend.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final SupabaseClient supabaseClient;

    public UserRepository(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    public User findByEmail(String email) {
        return supabaseClient.getOne(
                "users?email=eq." + email + "&select=*",
                new TypeReference<List<User>>() {}
        );
    }

    public User create(String name, String email, String passwordHash) {
        Map<String, Object> body = Map.of(
                "name", name,
                "email", email,
                "password_hash", passwordHash
        );

        return supabaseClient.post(
                "users",
                body,
                new TypeReference<List<User>>() {}
        );
    }
}
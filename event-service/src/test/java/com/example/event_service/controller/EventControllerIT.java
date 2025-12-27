package com.example.event_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("event_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.flyway.enabled", () -> true);
    }

    @Autowired MockMvc mvc;

    @Test
    void createEvent_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/admin/events")
                        .contentType("application/json")
                        .content("""
                   {"movieTitle":"X","startTime":"2025-12-30T20:00:00","pricePerTicket":100,"availableSeats":10}
                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createEvent_withAdminRole_returns200() throws Exception {
        mvc.perform(post("/api/admin/events")
                        .with(jwt().jwt(j -> j.claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))))
                        .contentType("application/json")
                        .content("""
                   {"movieTitle":"Interstellar","startTime":"2025-12-30T20:00:00","pricePerTicket":500,"availableSeats":10}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.movieTitle").value("Interstellar"));
    }
}
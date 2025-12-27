package com.example.booking_service.controller;

import com.example.booking_service.client.EventClient;
import com.example.booking_service.client.EventResponse;
import com.example.booking_service.service.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("booking_db")
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

    @MockBean EventClient eventClient;
    @MockBean KafkaProducer kafkaProducer;

    @Test
    void createBooking_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content("""
                  {"eventId":1,"customerName":"A","customerEmail":"a@b.com","numberOfTickets":1}
                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createBooking_withUserJwt_returns200() throws Exception {
        when(eventClient.getEvent(1L)).thenReturn(
                new EventResponse(1L, "Movie", LocalDateTime.now().plusDays(1), new BigDecimal("100.00"), 10)
        );
        when(eventClient.reserveSeats(eq(1L), eq(1), anyLong())).thenReturn(true);

        mvc.perform(post("/api/bookings")
                        .with(jwt().jwt(j -> j.claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER")))))
                        .contentType("application/json")
                        .content("""
                  {"eventId":1,"customerName":"Aziza","customerEmail":"aziza@example.com","numberOfTickets":1}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Aziza"))
                .andExpect(jsonPath("$.totalPrice").value(100.00));
    }
}
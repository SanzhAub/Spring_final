package com.example.booking_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.booking_service.dto.ReserveSeatsRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventClient {

    private final RestClient restClient;

    @Value("${event-service.base-url:http://event-service:8082}")
    private String baseUrl;

    public EventResponse getEvent(Long eventId) {
        return restClient.get()
                .uri(baseUrl + "/public/events/{id}", eventId)
                .retrieve()
                .body(EventResponse.class);
    }
    
    public boolean reserveSeats(Long eventId, Integer numberOfTickets, Long bookingId) {
        try {
            ReserveSeatsRequest request = new ReserveSeatsRequest(numberOfTickets, bookingId);
            
            // Получаем текущий JWT токен
            String token = getCurrentJwtToken();
            
            RestClient.RequestBodySpec requestSpec = restClient.post()
                    .uri(baseUrl + "/api/events/{eventId}/reserve", eventId)
                    .body(request);
            
            // Добавляем заголовок Authorization с токеном
            if (token != null) {
                requestSpec.header("Authorization", "Bearer " + token);
            }
            
            ResponseEntity<Void> response = requestSpec
                    .retrieve()
                    .toBodilessEntity();
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Failed to reserve seats for event {}: {}", eventId, e.getMessage());
            return false;
        }
    }
    
    private String getCurrentJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            return jwtToken.getToken().getTokenValue();
        }
        return null;
    }
    
    public void cancelReservation(Long eventId, Integer numberOfTickets, Long bookingId) {
        try {
            ReserveSeatsRequest request = new ReserveSeatsRequest(numberOfTickets, bookingId);
            
            // Получаем текущий JWT токен
            String token = getCurrentJwtToken();
            
            RestClient.RequestBodySpec requestSpec = restClient.post()
                    .uri(baseUrl + "/api/events/{eventId}/cancel-reservation", eventId)
                    .body(request);
            
            // Добавляем заголовок Authorization с токеном
            if (token != null) {
                requestSpec.header("Authorization", "Bearer " + token);
            }
            
            requestSpec
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to cancel reservation for event {}: {}", eventId, e.getMessage());
        }
    }
}
package com.example.booking_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
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
}

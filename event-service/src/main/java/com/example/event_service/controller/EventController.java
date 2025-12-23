package com.example.event_service.controller;

import com.example.event_service.dto.CreateEventRequest;
import com.example.event_service.dto.EventResponse;
import com.example.event_service.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class EventController {

    private final EventService eventService;

    // Public catalog
    @GetMapping("/public/events")
    public List<EventResponse> list() {
        return eventService.list();
    }

    @GetMapping("/public/events/{id}")
    public EventResponse get(@PathVariable Long id) {
        return eventService.getById(id);
    }

    // Admin-only later (Keycloak); keep endpoint already separated
    @PostMapping("/api/admin/events")
    public EventResponse create(@Valid @RequestBody CreateEventRequest req) {
        return eventService.create(req);
    }

    @GetMapping("/public/health")
    public String health() { return "OK"; }
}

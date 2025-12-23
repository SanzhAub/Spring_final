package com.example.event_service.service;

import com.example.event_service.dto.CreateEventRequest;
import com.example.event_service.dto.EventResponse;
import com.example.event_service.entity.Event;
import com.example.event_service.exception.NotFoundException;
import com.example.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse create(CreateEventRequest req) {
        Event e = new Event();
        e.setMovieTitle(req.movieTitle());
        e.setStartTime(req.startTime());
        e.setPricePerTicket(req.pricePerTicket());
        e.setAvailableSeats(req.availableSeats());
        Event saved = eventRepository.save(e);
        return toResponse(saved);
    }

    public EventResponse getById(Long id) {
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found"));
        return toResponse(e);
    }

    public List<EventResponse> list() {
        return eventRepository.findAll().stream().map(this::toResponse).toList();
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(e.getId(), e.getMovieTitle(), e.getStartTime(), e.getPricePerTicket(), e.getAvailableSeats());
    }
}

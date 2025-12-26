package com.example.event_service.service;

import com.example.event_service.dto.CreateEventRequest;
import com.example.event_service.dto.EventResponse;
import com.example.event_service.entity.Event;
import com.example.event_service.exception.NotFoundException;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.event_service.entity.Seat;
import com.example.event_service.dto.SeatResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public EventResponse create(CreateEventRequest req) {
        Event e = new Event();
        e.setMovieTitle(req.movieTitle());
        e.setStartTime(req.startTime());
        e.setPricePerTicket(req.pricePerTicket());
        e.setAvailableSeats(req.availableSeats());
        Event saved = eventRepository.save(e);

        initializeSeats(saved); // создаем места
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

    @Transactional
    public EventResponse update(Long id, CreateEventRequest req) {
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found"));

        e.setMovieTitle(req.movieTitle());
        e.setStartTime(req.startTime());
        e.setPricePerTicket(req.pricePerTicket());
        e.setAvailableSeats(req.availableSeats());

        Event updated = eventRepository.save(e);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found"));
        eventRepository.delete(e);
    }

    @Transactional
    public void initializeSeats(Event event) {
        for (int i = 1; i <= event.getAvailableSeats(); i++) {
            Seat seat = new Seat();
            seat.setEvent(event);
            seat.setSeatNumber(i);   // ⬅️ ВАЖНО
            seat.setStatus("AVAILABLE");
            seatRepository.save(seat);
        }
        
    }

    private EventResponse toResponse(Event e) {
        List<SeatResponse> seats = seatRepository.findByEventId(e.getId()).stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNumber(), s.getStatus()))
                .toList();
        return new EventResponse(
                e.getId(),
                e.getMovieTitle(),
                e.getStartTime(),
                e.getPricePerTicket(),
                e.getAvailableSeats(),
                seats
        );
    }

        @Transactional
    public boolean reserveSeats(Long eventId, Integer numberOfTickets, Long bookingId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
        
        List<Seat> availableSeats = seatRepository.findByEventIdAndStatus(eventId, "AVAILABLE");
        
        if (availableSeats.size() < numberOfTickets) {
            return false;
        }
        
        List<Seat> seatsToReserve = availableSeats.stream()
                .limit(numberOfTickets)
                .collect(Collectors.toList());
        
        for (Seat seat : seatsToReserve) {
            seat.setStatus("RESERVED");
            seat.setBookingId(bookingId);
        }
        
        seatRepository.saveAll(seatsToReserve);
        
        event.setAvailableSeats(event.getAvailableSeats() - numberOfTickets);
        eventRepository.save(event);
        
        return true;
    }

    @Transactional
    public void cancelReservation(Long eventId, Integer numberOfTickets, Long bookingId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
        
        List<Seat> reservedSeats = seatRepository.findByBookingId(bookingId);
        
        List<Seat> seatsToRelease = reservedSeats.stream()
                .limit(numberOfTickets)
                .collect(Collectors.toList());
        
        for (Seat seat : seatsToRelease) {
            seat.setStatus("AVAILABLE");
            seat.setBookingId(null);
        }
        
        seatRepository.saveAll(seatsToRelease);
        
        event.setAvailableSeats(event.getAvailableSeats() + seatsToRelease.size());
        eventRepository.save(event);
    }
}

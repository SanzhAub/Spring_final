package com.example.event_service.service;

import com.example.event_service.entity.Event;
import com.example.event_service.entity.Seat;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceReserveSeatsTest {

    @Mock EventRepository eventRepository;
    @Mock SeatRepository seatRepository;

    @InjectMocks EventService eventService;

    @Test
    void reserveSeats_success_decrementsAvailable_andReservesSeats() {
        Event event = new Event();
        event.setId(1L);
        event.setAvailableSeats(3);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Seat s1 = new Seat(); s1.setStatus("AVAILABLE");
        Seat s2 = new Seat(); s2.setStatus("AVAILABLE");
        when(seatRepository.findByEventIdAndStatus(1L, "AVAILABLE")).thenReturn(List.of(s1, s2));

        boolean ok = eventService.reserveSeats(1L, 2, 77L);
        assertTrue(ok);

        verify(seatRepository).saveAll(argThat(list ->
                StreamSupport.stream(list.spliterator(), false).count() == 2
        ));
        verify(eventRepository).save(argThat(e -> e.getAvailableSeats() == 1));
    }

    @Test
    void reserveSeats_notEnoughSeats_returnsFalse() {
        Event event = new Event();
        event.setId(1L);
        event.setAvailableSeats(1);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(seatRepository.findByEventIdAndStatus(1L, "AVAILABLE")).thenReturn(List.of());

        boolean ok = eventService.reserveSeats(1L, 2, 77L);
        assertFalse(ok);

        verify(eventRepository, never()).save(any());
        verify(seatRepository, never()).saveAll(any());
    }
}
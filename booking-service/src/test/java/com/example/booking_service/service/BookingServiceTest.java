package com.example.booking_service.service;

import com.example.booking_service.client.EventClient;
import com.example.booking_service.client.EventResponse;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.exception.BadRequestException;
import com.example.booking_service.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock KafkaProducer kafkaProducer;
    @Mock EventClient eventClient;

    @InjectMocks BookingService bookingService;

    @Test
    void createBooking_success_reservesSeats_andPublishesKafka() {
        // given
        Long eventId = 10L;
        CreateBookingRequest req = new CreateBookingRequest(
                eventId, "Aziza", "aziza@example.com", 2
        );

        EventResponse event = new EventResponse(
                eventId,
                "Interstellar",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("500.00"),
                50
        );

        when(eventClient.getEvent(eventId)).thenReturn(event);

        // save should return booking with ID
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        when(eventClient.reserveSeats(eventId, 2, 1L)).thenReturn(true);

        // when
        var resp = bookingService.createBooking(req);

        // then
        assertNotNull(resp.id());
        assertEquals(eventId, resp.eventId());
        assertEquals("Aziza", resp.customerName());
        assertEquals(new BigDecimal("1000.00"), resp.totalPrice());

        verify(eventClient).reserveSeats(eventId, 2, 1L);
        verify(kafkaProducer).sendBookingEvent(any());
        verify(bookingRepository, never()).delete(any());
    }

    @Test
    void createBooking_notEnoughSeats_throwsBadRequest() {
        Long eventId = 10L;
        CreateBookingRequest req = new CreateBookingRequest(
                eventId, "Aziza", "aziza@example.com", 999
        );

        EventResponse event = new EventResponse(
                eventId,
                "Interstellar",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("500.00"),
                3
        );

        when(eventClient.getEvent(eventId)).thenReturn(event);

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(req));
        verifyNoInteractions(kafkaProducer);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_reservationFailed_deletesBooking_andThrows() {
        Long eventId = 10L;
        CreateBookingRequest req = new CreateBookingRequest(
                eventId, "Aziza", "aziza@example.com", 2
        );

        EventResponse event = new EventResponse(
                eventId,
                "Interstellar",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("500.00"),
                50
        );

        when(eventClient.getEvent(eventId)).thenReturn(event);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        when(eventClient.reserveSeats(eventId, 2, 1L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(req));

        verify(bookingRepository, times(2)).delete(argThat(b -> b.getId().equals(1L)));
        verify(kafkaProducer, never()).sendBookingEvent(any());
    }
}
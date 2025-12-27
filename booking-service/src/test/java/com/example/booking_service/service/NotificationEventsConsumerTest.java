package com.example.booking_service.service;

import com.example.booking_service.entity.Booking;
import com.example.booking_service.repository.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventsConsumerTest {

    @Mock BookingRepository bookingRepository;

    private NotificationEventsConsumer consumer;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // важно для sentAt
        consumer = new NotificationEventsConsumer(mapper, bookingRepository);
    }

    @Test
    void consume_statusSENT_updatesBookingToNOTIFIED() {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setStatus("CONFIRMED");

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        String json = """
          {"bookingId":5,"customerEmail":"a@b.com","status":"SENT","sentAt":"2025-12-27T12:00:00"}
        """;

        consumer.consume(json);

        verify(bookingRepository).findById(5L);
        verify(bookingRepository).save(argThat(b -> "NOTIFIED".equals(b.getStatus())));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void consume_statusFAILED_updatesBookingToNOTIFICATION_FAILED() {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setStatus("CONFIRMED");

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        String json = """
          {"bookingId":5,"customerEmail":"a@b.com","status":"FAILED","sentAt":"2025-12-27T12:00:00"}
        """;

        consumer.consume(json);

        verify(bookingRepository).findById(5L);
        verify(bookingRepository).save(argThat(b -> "NOTIFICATION_FAILED".equals(b.getStatus())));
        verifyNoMoreInteractions(bookingRepository);
    }
}
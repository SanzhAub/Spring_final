package com.example.booking_service.service;

import com.example.booking_service.client.EventClient;
import com.example.booking_service.client.EventResponse;
import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.event.BookingCreatedEvent;
import com.example.booking_service.exception.BadRequestException;
import com.example.booking_service.exception.NotFoundException;
import com.example.booking_service.model.BookingStatus;
import com.example.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final KafkaProducer kafkaProducer;
    private final EventClient eventClient;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest req) {
        // 1) Validate event via REST
        EventResponse event = eventClient.getEvent(req.eventId());
        if (event == null) {
            throw new NotFoundException("Event with id=" + req.eventId() + " not found");
        }
        if (event.availableSeats() != null && req.numberOfTickets() > event.availableSeats()) {
            throw new BadRequestException("Not enough seats for event id=" + req.eventId());
        }

        // 2) Persist booking
        Booking booking = new Booking();
        booking.setEventId(req.eventId());
        booking.setMovieTitle(event.movieTitle());
        booking.setEventStartTime(event.startTime());
        booking.setPricePerTicket(event.pricePerTicket());

        booking.setCustomerName(req.customerName());
        booking.setCustomerEmail(req.customerEmail());
        booking.setNumberOfTickets(req.numberOfTickets());

        BigDecimal total = event.pricePerTicket()
                .multiply(BigDecimal.valueOf(req.numberOfTickets()));

        booking.setTotalPrice(total);
        booking.setStatus(BookingStatus.CONFIRMED.name());

        Booking saved = bookingRepository.save(booking);

        // 3) Publish Kafka event BookingCreated
        BookingCreatedEvent evt = new BookingCreatedEvent();
        evt.setBookingId(saved.getId());
        evt.setEventId(saved.getEventId());
        evt.setMovieTitle(saved.getMovieTitle());
        evt.setCustomerEmail(saved.getCustomerEmail());
        evt.setNumberOfTickets(saved.getNumberOfTickets());
        evt.setBookingTime(saved.getBookingTime());

        kafkaProducer.sendBookingEvent(evt);

        return toResponse(saved);
    }

    public BookingResponse getBooking(Long id) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + id + " not found"));
        return toResponse(b);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException("Booking with id=" + id + " not found");
        }
        bookingRepository.deleteById(id);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getEventId(),
                b.getMovieTitle(),
                b.getEventStartTime(),
                b.getCustomerName(),
                b.getCustomerEmail(),
                b.getNumberOfTickets(),
                b.getPricePerTicket(),
                b.getTotalPrice(),
                b.getBookingTime(),
                b.getStatus()
        );
    }
}

package com.example.booking_service.service;
import com.example.booking_service.entity.Booking;
import com.example.booking_service.event.BookingCreatedEvent;
import com.example.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final KafkaProducer kafkaProducer;
    
    @Transactional
    public Booking createBooking(Booking booking) {
        booking.setTotalPrice(booking.getNumberOfTickets() * 10.0);
        booking.setStatus("CONFIRMED");
        
        Booking savedBooking = bookingRepository.save(booking);
        
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(savedBooking.getId());
        event.setMovieTitle(savedBooking.getMovieTitle());
        event.setCustomerEmail(savedBooking.getCustomerEmail());
        event.setNumberOfTickets(savedBooking.getNumberOfTickets());
        event.setBookingTime(savedBooking.getBookingTime());
        
        kafkaProducer.sendBookingEvent(event);
        
        return savedBooking;
    }
    
    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    @Transactional
    public boolean deleteBooking(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
package com.eventbooking.service;

import com.eventbooking.dto.BookingResponse;
import com.eventbooking.dto.CreateBookingRequest;
import com.eventbooking.exception.BusinessException;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.model.Booking;
import com.eventbooking.model.Event;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository   eventRepository;
    private final EventService      eventService;

    @Transactional
    public BookingResponse createBooking(Long eventId, CreateBookingRequest request) {
        Event event = eventService.findEventOrThrow(eventId);

        // Rule 1: Event must be OPEN
        if (event.getStatus() == Event.EventStatus.CLOSED) {
            throw new BusinessException("Cannot book a seat — event is CLOSED.");
        }

        // Rule 2: Seats must be available
        if (!event.hasAvailableSeats()) {
            throw new BusinessException("Cannot book a seat — event has no remaining capacity.");
        }

        // Rule 3: Duplicate booking prevention
        if (bookingRepository.existsByEventIdAndAttendeeEmail(eventId, request.attendeeEmail())) {
            throw new BusinessException(
                    "Attendee with email '" + request.attendeeEmail() + "' has already booked this event.");
        }

        // Reserve the seat (and auto-close the event if now fully booked)
        event.reserveSeat();
        eventRepository.save(event);

        Booking booking = Booking.builder()
                .event(event)
                .attendeeName(request.attendeeName())
                .attendeeEmail(request.attendeeEmail())
                .build();

        Booking saved = bookingRepository.save(booking);
        return BookingResponse.from(saved);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.booking(bookingId));

        Event event = booking.getEvent();
        event.releaseSeat();
        eventRepository.save(event);

        bookingRepository.delete(booking);
    }


    public Page<BookingResponse> getBookingsForEvent(Long eventId, Pageable pageable) {
        // Ensure the event exists before querying bookings
        eventService.findEventOrThrow(eventId);
        return bookingRepository.findByEventId(eventId, pageable).map(BookingResponse::from);
    }
}
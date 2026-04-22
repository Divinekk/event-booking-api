package com.eventbooking.controller;

import com.eventbooking.dto.BookingResponse;
import com.eventbooking.dto.CreateBookingRequest;
import com.eventbooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Book seats, cancel bookings, and view bookings for an event")
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary     = "Book a seat at an event",
            description = "Creates a booking for the given event. Fails if the event is CLOSED, " +
                    "fully booked, or the email has already booked this event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PostMapping("/events/{id}/bookings")
    public ResponseEntity<BookingResponse> bookSeat(
            @Parameter(description = "ID of the event to book", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CreateBookingRequest request) {

        BookingResponse booking = bookingService.createBooking(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @Operation(
            summary     = "Cancel a booking",
            description = "Deletes the booking and frees up one seat on the event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> cancelBooking(
            @Parameter(description = "ID of the booking to cancel", required = true)
            @PathVariable Long id) {

        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary     = "List all bookings for an event",
            description = "Returns a paginated list of all bookings associated with the given event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of bookings"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/events/{id}/bookings")
    public ResponseEntity<Page<BookingResponse>> getBookingsForEvent(
            @Parameter(description = "ID of the event", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "bookedAt") Pageable pageable) {

        return ResponseEntity.ok(bookingService.getBookingsForEvent(id, pageable));
    }
}
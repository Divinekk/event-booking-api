package com.eventbooking.dto;

import com.eventbooking.model.Booking;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Booking details returned by the API")
public record BookingResponse(

        @Schema(description = "Unique identifier of the booking", example = "1")
        Long id,

        @Schema(description = "ID of the event that was booked", example = "5")
        Long eventId,

        @Schema(description = "Title of the booked event", example = "Tech Conference 2025")
        String eventTitle,

        @Schema(description = "Full name of the attendee", example = "Jane Doe")
        String attendeeName,

        @Schema(description = "Email address of the attendee", example = "jane.doe@example.com")
        String attendeeEmail,

        @Schema(description = "Timestamp when the booking was made", example = "2025-09-01T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime bookedAt
) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getEvent().getId(),
                booking.getEvent().getTitle(),
                booking.getAttendeeName(),
                booking.getAttendeeEmail(),
                booking.getBookedAt()
        );
    }
}
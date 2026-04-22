package com.eventbooking.dto;

import com.eventbooking.model.Event;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Event details returned by the API")
public record EventResponse(

        @Schema(description = "Unique identifier of the event", example = "1")
        Long id,

        @Schema(description = "Title of the event", example = "Tech Conference 2025")
        String title,

        @Schema(description = "Description of the event")
        String description,

        @Schema(description = "Date and time of the event", example = "2025-12-15T10:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date,

        @Schema(description = "Venue of the event", example = "Landmark Centre, Lagos")
        String venue,

        @Schema(description = "Total number of seats available", example = "100")
        int totalSeats,

        @Schema(description = "Number of seats currently booked", example = "42")
        int bookedSeats,

        @Schema(description = "Number of seats still available", example = "58")
        int availableSeats,

        @Schema(description = "Current status of the event", example = "OPEN")
        String status
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getVenue(),
                event.getTotalSeats(),
                event.getBookedSeats(),
                event.getTotalSeats() - event.getBookedSeats(),
                event.getStatus().name()
        );
    }
}
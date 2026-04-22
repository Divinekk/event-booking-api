package com.eventbooking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Request body for creating a new event")
public record CreateEventRequest(

        @Schema(description = "Title of the event", example = "Tech Conference 2025")
        @NotBlank(message = "Title is required")
        String title,

        @Schema(description = "Description of the event", example = "Annual technology conference covering AI, cloud, and DevOps.")
        @NotBlank(message = "Description is required")
        String description,

        @Schema(description = "Date and time of the event (must be in the future)", example = "2025-12-15T10:00:00")
        @NotNull(message = "Date is required")
        @Future(message = "Event date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date,

        @Schema(description = "Venue where the event will be held", example = "Landmark Centre, Lagos")
        @NotBlank(message = "Venue is required")
        String venue,

        @Schema(description = "Total number of available seats", example = "100")
        @NotNull(message = "Total seats is required")
        @Positive(message = "Total seats must be greater than 0")
        Integer totalSeats
) {}
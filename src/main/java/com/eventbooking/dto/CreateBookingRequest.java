package com.eventbooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for booking a seat at an event")
public record CreateBookingRequest(

        @Schema(description = "Full name of the attendee", example = "Jane Doe")
        @NotBlank(message = "Attendee name is required")
        String attendeeName,

        @Schema(description = "Valid email address of the attendee", example = "jane.doe@example.com")
        @NotBlank(message = "Attendee email is required")
        @Email(message = "Attendee email must be a valid email address")
        String attendeeEmail
) {}
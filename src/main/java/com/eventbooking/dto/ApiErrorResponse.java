package com.eventbooking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Standard error response body")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Short error type label", example = "Bad Request")
        String error,

        @Schema(description = "Human-readable message describing the error", example = "Event is fully booked")
        String message,

        @Schema(description = "API path that produced the error", example = "/events/1/bookings")
        String path,

        @Schema(description = "Timestamp when the error occurred")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "Field-level validation errors (present on 400 validation failures)")
        List<FieldError> fieldErrors
) {

        public record FieldError(
                @Schema(description = "Name of the field that failed validation", example = "attendeeEmail")
                String field,

                @Schema(description = "Validation failure message", example = "must be a valid email address")
                String message
        ) {}
}
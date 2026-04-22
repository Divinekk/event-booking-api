package com.eventbooking.controller;

import com.eventbooking.dto.CreateEventRequest;
import com.eventbooking.dto.EventResponse;
import com.eventbooking.service.EventService;
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
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Create and retrieve events")
public class EventController {

    private final EventService eventService;

    @Operation(
            summary     = "Create a new event",
            description = "Creates an event with an initial status of OPEN. " +
                    "The date must be in the future and totalSeats must be positive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error — check field errors in response")
    })
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request) {

        EventResponse created = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary     = "List all events",
            description = "Returns a paginated list of all events. " +
                    "Use ?page=0&size=10&sort=date,asc to control pagination."
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of events")
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {

        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @Operation(
            summary     = "Get event by ID",
            description = "Returns the full details of a single event, including current booking counts."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "ID of the event to retrieve", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(eventService.getEventById(id));
    }
}
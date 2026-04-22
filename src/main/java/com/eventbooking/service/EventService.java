package com.eventbooking.service;

import com.eventbooking.dto.CreateEventRequest;
import com.eventbooking.dto.EventResponse;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.model.Event;
import com.eventbooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .date(request.date())
                .venue(request.venue())
                .totalSeats(request.totalSeats())
                .bookedSeats(0)
                .status(Event.EventStatus.OPEN)
                .build();

        Event saved = eventRepository.save(event);
        return EventResponse.from(saved);
    }

    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventResponse::from);
    }

    public EventResponse getEventById(Long id) {
        Event event = findEventOrThrow(id);
        return EventResponse.from(event);
    }

    public Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.event(id));
    }
}
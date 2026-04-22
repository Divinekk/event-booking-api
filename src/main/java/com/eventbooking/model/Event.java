package com.eventbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int bookedSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    public enum EventStatus {
        OPEN, CLOSED
    }

    /**
     * Checks whether there is at least one seat remaining.
     */
    public boolean hasAvailableSeats() {
        return bookedSeats < totalSeats;
    }

    /**
     * Increments bookedSeats and automatically closes the event if fully booked.
     */
    public void reserveSeat() {
        bookedSeats++;
        if (bookedSeats >= totalSeats) {
            status = EventStatus.CLOSED;
        }
    }

    /**
     * Decrements bookedSeats and reopens the event if it was CLOSED due to capacity.
     */
    public void releaseSeat() {
        if (bookedSeats > 0) {
            bookedSeats--;
        }
        if (status == EventStatus.CLOSED && bookedSeats < totalSeats) {
            status = EventStatus.OPEN;
        }
    }
}
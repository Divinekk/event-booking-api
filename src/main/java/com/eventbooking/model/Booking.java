package com.eventbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = {
                // Enforce the "same email cannot book the same event twice" rule at the DB level
                @UniqueConstraint(
                        name = "uq_booking_event_email",
                        columnNames = {"event_id", "attendee_email"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "attendee_name", nullable = false)
    private String attendeeName;

    @Column(name = "attendee_email", nullable = false)
    private String attendeeEmail;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    @PrePersist
    protected void onCreate() {
        bookedAt = LocalDateTime.now();
    }
}
# Event Booking API

A RESTful backend API built with **Spring Boot 3** that allows users to create events and manage seat bookings.

---

## Technology Stack

| Layer            | Technology                        |
|------------------|-----------------------------------|
| Language         | Java 17                           |
| Framework        | Spring Boot 3.2.5                 |
| Persistence      | Spring Data JPA + H2 (in-memory)  |
| Validation       | Jakarta Bean Validation           |
| API Docs         | Springdoc OpenAPI 2 / Swagger UI  |
| Build Tool       | Maven                             |
| Testing          | JUnit 5 + Mockito + AssertJ       |
| Utilities        | Lombok                            |

---

## Project Structure

```
event-booking-api/
├── src/
│   ├── main/
│   │   ├── java/com/eventbooking/
│   │   │   ├── EventBookingApplication.java      # Entry point
│   │   │   ├── controller/
│   │   │   │   ├── EventController.java          # POST/GET /events
│   │   │   │   └── BookingController.java        # POST/GET/DELETE /bookings
│   │   │   ├── dto/
│   │   │   │   ├── ApiErrorResponse.java
│   │   │   │   ├── BookingResponse.java
│   │   │   │   ├── CreateBookingRequest.java
│   │   │   │   ├── CreateEventRequest.java
│   │   │   │   └── EventResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── Booking.java
│   │   │   │   └── Event.java
│   │   │   ├── repository/
│   │   │   │   ├── BookingRepository.java
│   │   │   │   └── EventRepository.java
│   │   │   └── service/
│   │   │       ├── BookingService.java
│   │   │       └── EventService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/eventbooking/service/
│       ├── EventServiceTest.java
│       ├── BookingServiceTest.java
│       └── EventEntityTest.java
├── pom.xml
└── README.md
```

---

## How to Build

### Prerequisites

- **Java 17+** — verify with `java -version`
- **Maven 3.8+** — verify with `mvn -version`
  *(or use the included Maven Wrapper: `./mvnw`)*

### Build

```bash
git clone https://github.com/Divinekk/event-booking-api.git
cd event-booking-api
mvn clean install
```

---

## How to Run

```bash
mvn spring-boot:run
```

The application starts at `http://localhost:8080`


---

## How to Access Swagger UI

With the application running, open:

```
http://localhost:8080/swagger-ui.html
```

All endpoints are visible, documented, and **fully testable** directly in the browser.
The raw OpenAPI JSON spec is at `http://localhost:8080/api-docs`.

> Note: This runs locally. Clone and run to test via Swagger.

---

## API Reference

### Events

| Method | Endpoint        | Description              | Status Codes    |
|--------|-----------------|--------------------------|-----------------|
| POST   | `/events`        | Create a new event       | 201, 400        |
| GET    | `/events`        | List all events (paged)  | 200             |
| GET    | `/events/{id}`   | Get event by ID          | 200, 404        |

#### Create Event — Request Body

```json
{
  "title": "Tech Conference 2027",
  "description": "Annual conference",
  "date": "2027-06-01T10:00:00",
  "venue": "Landmark Centre, Lagos",
  "totalSeats": 100
}
```

#### Create Event — Response (201)

```json
{
  "id": 1,
  "title": "Tech Conference 2027",
  "description": "Annual technology conference.",
  "date": "2027-06-01T10:00:00",
  "venue": "Landmark Centre, Lagos",
  "totalSeats": 100,
  "bookedSeats": 0,
  "availableSeats": 100,
  "status": "OPEN"
}
```

---

### Bookings

| Method | Endpoint                  | Description                     | Status Codes    |
|--------|---------------------------|---------------------------------|-----------------|
| POST   | `/events/{id}/bookings`    | Book a seat at an event         | 201, 400, 404   |
| GET    | `/events/{id}/bookings`    | List bookings for an event      | 200, 404        |
| DELETE | `/bookings/{id}`           | Cancel a booking                | 204, 404        |

#### Book a Seat — Request Body

```json
{
  "attendeeName": "Jane Doe",
  "attendeeEmail": "jane.doe@example.com"
}
```

#### Book a Seat — Response (201)

```json
{
  "id": 1,
  "eventId": 1,
  "eventTitle": "Tech Conference 2027",
  "attendeeName": "Jane Doe",
  "attendeeEmail": "jane.doe@example.com",
  "bookedAt": "2027-04-22T09:15:00"
}
```

---

### Pagination

List endpoints support standard Spring pagination query parameters:

```
GET /events?page=0&size=10&sort=date,asc
GET /events/1/bookings?page=0&size=5&sort=bookedAt,desc
```

---

### Error Response Format

All errors follow a consistent envelope:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot book a seat — event is CLOSED.",
  "path": "/events/1/bookings",
  "timestamp": "2026-04-22T09:00:00",
  "fieldErrors": null
}
```

Validation errors include a `fieldErrors` array:

```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields failed validation",
  "path": "/events",
  "timestamp": "2026-04-22T09:00:00",
  "fieldErrors": [
    { "field": "totalSeats", "message": "Total seats must be greater than 0" },
    { "field": "attendeeEmail", "message": "must be a valid email address" }
  ]
}
```

---

## Business Rules

| Rule | Implementation |
|------|----------------|
| Event date must be in the future | `@Future` annotation on `CreateEventRequest.date` |
| `totalSeats` must be > 0 | `@Positive` annotation on `CreateEventRequest.totalSeats` |
| `attendeeEmail` must be valid format | `@Email` annotation on `CreateBookingRequest.attendeeEmail` |
| Cannot book a CLOSED event | Checked in `BookingService.createBooking()` |
| Cannot book when `bookedSeats >= totalSeats` | Checked via `Event.hasAvailableSeats()` |
| Same email cannot book the same event twice | DB unique constraint + `BookingRepository.existsByEventIdAndAttendeeEmail()` |
| Cancelling a booking frees a seat | `Event.releaseSeat()` called in `BookingService.cancelBooking()` |
| Event auto-closes when fully booked *(bonus)* | `Event.reserveSeat()` sets status to `CLOSED` when `bookedSeats == totalSeats` |
| Event re-opens when a seat is released *(bonus)* | `Event.releaseSeat()` sets status back to `OPEN` if capacity allows |

---

## Assumptions

- In-memory storage resets on restart
- Event status auto-updates to `CLOSED` when fully booked
- Event status resets to `OPEN` when a booking is cancelled and capacity is available

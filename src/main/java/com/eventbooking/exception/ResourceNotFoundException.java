package com.eventbooking.exception;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException event(Long id) {
        return new ResourceNotFoundException("Event not found with id: " + id);
    }

    public static ResourceNotFoundException booking(Long id) {
        return new ResourceNotFoundException("Booking not found with id: " + id);
    }
}
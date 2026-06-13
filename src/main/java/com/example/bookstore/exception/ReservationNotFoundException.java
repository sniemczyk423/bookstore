package com.example.bookstore.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(Long id) {
        super("reservation with id " + id + " was not found");
    }
}
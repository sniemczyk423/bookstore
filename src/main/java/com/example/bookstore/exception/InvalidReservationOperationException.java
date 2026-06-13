package com.example.bookstore.exception;

public class InvalidReservationOperationException extends RuntimeException {

    public InvalidReservationOperationException(String message) {
        super(message);
    }
}
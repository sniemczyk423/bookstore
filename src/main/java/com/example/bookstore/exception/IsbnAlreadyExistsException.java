package com.example.bookstore.exception;

public class IsbnAlreadyExistsException extends RuntimeException {

    public IsbnAlreadyExistsException(String isbn) {
        super("book with isbn " + isbn + " already exists");
    }
}
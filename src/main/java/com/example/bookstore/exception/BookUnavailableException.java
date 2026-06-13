package com.example.bookstore.exception;

public class BookUnavailableException extends RuntimeException {

    public BookUnavailableException(Long bookId) {
        super("book with id " + bookId + " is unavailable");
    }
}
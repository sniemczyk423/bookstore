package com.example.bookstore.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("book with id " + id + " was not found");
    }
}
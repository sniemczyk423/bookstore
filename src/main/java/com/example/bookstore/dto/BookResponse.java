package com.example.bookstore.dto;

import java.time.LocalDateTime;

public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String description;
    private Integer availableCopies;
    private LocalDateTime createdAt;

    public BookResponse(Long id, String title, String author, String isbn, String category,
                        String description, Integer availableCopies, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.description = description;
        this.availableCopies = availableCopies;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
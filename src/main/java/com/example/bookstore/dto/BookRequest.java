package com.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookRequest {

    @NotBlank(message = "title is required")
    @Size(max = 200, message = "title cannot be longer than 200 characters")
    private String title;

    @NotBlank(message = "author is required")
    @Size(max = 150, message = "author cannot be longer than 150 characters")
    private String author;

    @NotBlank(message = "isbn is required")
    @Size(max = 30, message = "isbn cannot be longer than 30 characters")
    private String isbn;

    @NotBlank(message = "category is required")
    @Size(max = 100, message = "category cannot be longer than 100 characters")
    private String category;

    private String description;

    @NotNull(message = "available copies value is required")
    @Min(value = 0, message = "available copies cannot be negative")
    private Integer availableCopies;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
}
package com.example.bookstore.dto;

import com.example.bookstore.entity.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String userEmail;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
    private LocalDateTime cancelledAt;

    public ReservationResponse(
            Long id,
            Long bookId,
            String bookTitle,
            String userEmail,
            ReservationStatus status,
            LocalDateTime reservedAt,
            LocalDateTime cancelledAt
    ) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userEmail = userEmail;
        this.status = status;
        this.reservedAt = reservedAt;
        this.cancelledAt = cancelledAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
}
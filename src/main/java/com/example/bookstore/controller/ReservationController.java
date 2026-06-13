package com.example.bookstore.controller;

import com.example.bookstore.dto.ReservationRequest;
import com.example.bookstore.dto.ReservationResponse;
import com.example.bookstore.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication
    ) {
        return reservationService.createReservation(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/my")
    public List<ReservationResponse> getMyReservations(
            Authentication authentication
    ) {
        return reservationService.getUserReservations(
                authentication.getName()
        );
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancelReservation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        return reservationService.cancelReservation(
                id,
                authentication.getName(),
                admin
        );
    }
}
package com.example.bookstore.controller;

import com.example.bookstore.dto.ReservationRequest;
import com.example.bookstore.dto.ReservationResponse;
import com.example.bookstore.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(
        name = "Reservations",
        description = "Book reservation management operations"
)
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Create a book reservation",
            description = "Creates an active reservation for the authenticated user"
    )
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

    @Operation(
            summary = "Get authenticated user's reservations",
            description = "Returns all reservations assigned to the currently authenticated user"
    )
    @GetMapping("/my")
    public List<ReservationResponse> getMyReservations(
            Authentication authentication
    ) {
        return reservationService.getUserReservations(
                authentication.getName()
        );
    }

    @Operation(
            summary = "Get all reservations",
            description = "Returns all reservations in the system. Available only to administrators"
    )
    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(
            summary = "Cancel a reservation",
            description = "Cancels an active reservation. Users can cancel their own reservations, while administrators can cancel any reservation"
    )
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
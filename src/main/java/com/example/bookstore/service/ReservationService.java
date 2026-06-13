package com.example.bookstore.service;

import com.example.bookstore.dto.ReservationRequest;
import com.example.bookstore.dto.ReservationResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Reservation;
import com.example.bookstore.entity.ReservationStatus;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.BookUnavailableException;
import com.example.bookstore.exception.InvalidReservationOperationException;
import com.example.bookstore.exception.ReservationNotFoundException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReservationRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(
            String email,
            ReservationRequest request
    ) {
        User user = findUserByEmail(email);

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(request.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException(book.getId());
        }

        boolean alreadyReserved =
                reservationRepository.existsByUserAndBookIdAndStatus(
                        user,
                        book.getId(),
                        ReservationStatus.ACTIVE
                );

        if (alreadyReserved) {
            throw new InvalidReservationOperationException(
                    "user already has an active reservation for this book"
            );
        }

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.ACTIVE
        );

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    public List<ReservationResponse> getUserReservations(String email) {
        User user = findUserByEmail(email);

        return reservationRepository.findByUserOrderByReservedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ReservationResponse cancelReservation(
            Long reservationId,
            String email,
            boolean admin
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationNotFoundException(reservationId)
                );

        if (!admin && !reservation.getUser().getEmail().equals(email)) {
            throw new InvalidReservationOperationException(
                    "user cannot cancel another user's reservation"
            );
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new InvalidReservationOperationException(
                    "only active reservations can be cancelled"
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);

        return mapToResponse(reservationRepository.save(reservation));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "user with email " + email + " was not found"
                        )
                );
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getBook().getId(),
                reservation.getBook().getTitle(),
                reservation.getUser().getEmail(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getCancelledAt()
        );
    }
}
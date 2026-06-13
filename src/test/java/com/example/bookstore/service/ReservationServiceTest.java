package com.example.bookstore.service;

import com.example.bookstore.dto.ReservationRequest;
import com.example.bookstore.dto.ReservationResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Reservation;
import com.example.bookstore.entity.ReservationStatus;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.BookUnavailableException;
import com.example.bookstore.exception.InvalidReservationOperationException;
import com.example.bookstore.exception.ReservationNotFoundException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReservationRepository;
import com.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final String USER_EMAIL = "user@bookstore.com";
    private static final Long BOOK_ID = 1L;
    private static final Long RESERVATION_ID = 1L;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldCreateReservation() {
        User user = createUser();
        Book book = createBook(3);
        ReservationRequest request = createRequest(BOOK_ID);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(BOOK_ID))
                .thenReturn(Optional.of(book));

        when(reservationRepository.existsByUserAndBookIdAndStatus(
                user,
                BOOK_ID,
                ReservationStatus.ACTIVE
        )).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationService.createReservation(
                USER_EMAIL,
                request
        );

        assertEquals("Clean Code", response.getBookTitle());
        assertEquals(USER_EMAIL, response.getUserEmail());
        assertEquals(ReservationStatus.ACTIVE, response.getStatus());
        assertEquals(2, book.getAvailableCopies());

        verify(bookRepository).save(book);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldRejectReservationWhenUserDoesNotExist() {
        ReservationRequest request = createRequest(BOOK_ID);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> reservationService.createReservation(
                        USER_EMAIL,
                        request
                )
        );

        verify(bookRepository, never()).findById(anyLong());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectReservationWhenBookDoesNotExist() {
        User user = createUser();
        ReservationRequest request = createRequest(99L);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookNotFoundException.class,
                () -> reservationService.createReservation(
                        USER_EMAIL,
                        request
                )
        );

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectReservationWhenBookIsUnavailable() {
        User user = createUser();
        Book book = createBook(0);
        ReservationRequest request = createRequest(BOOK_ID);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(BOOK_ID))
                .thenReturn(Optional.of(book));

        assertThrows(
                BookUnavailableException.class,
                () -> reservationService.createReservation(
                        USER_EMAIL,
                        request
                )
        );

        verify(
                reservationRepository,
                never()
        ).existsByUserAndBookIdAndStatus(
                any(),
                anyLong(),
                any()
        );

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateActiveReservation() {
        User user = createUser();
        Book book = createBook(3);
        ReservationRequest request = createRequest(BOOK_ID);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(BOOK_ID))
                .thenReturn(Optional.of(book));

        when(reservationRepository.existsByUserAndBookIdAndStatus(
                user,
                BOOK_ID,
                ReservationStatus.ACTIVE
        )).thenReturn(true);

        assertThrows(
                InvalidReservationOperationException.class,
                () -> reservationService.createReservation(
                        USER_EMAIL,
                        request
                )
        );

        assertEquals(3, book.getAvailableCopies());

        verify(bookRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserReservations() {
        User user = createUser();
        Book book = createBook(3);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.ACTIVE
        );

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(reservationRepository.findByUserOrderByReservedAtDesc(user))
                .thenReturn(List.of(reservation));

        List<ReservationResponse> result =
                reservationService.getUserReservations(USER_EMAIL);

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getBookTitle());
        assertEquals(USER_EMAIL, result.get(0).getUserEmail());
        assertEquals(
                ReservationStatus.ACTIVE,
                result.get(0).getStatus()
        );
    }

    @Test
    void shouldThrowExceptionWhenGettingReservationsForMissingUser() {
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> reservationService.getUserReservations(USER_EMAIL)
        );

        verify(
                reservationRepository,
                never()
        ).findByUserOrderByReservedAtDesc(any());
    }

    @Test
    void shouldReturnAllReservations() {
        User user = createUser();
        Book book = createBook(3);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.ACTIVE
        );

        when(reservationRepository.findAll())
                .thenReturn(List.of(reservation));

        List<ReservationResponse> result =
                reservationService.getAllReservations();

        assertEquals(1, result.size());
        assertEquals(USER_EMAIL, result.get(0).getUserEmail());
        assertEquals("Clean Code", result.get(0).getBookTitle());
    }

    @Test
    void shouldReturnEmptyListWhenNoReservationsExist() {
        when(reservationRepository.findAll())
                .thenReturn(List.of());

        List<ReservationResponse> result =
                reservationService.getAllReservations();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCancelOwnReservation() {
        User user = createUser();
        Book book = createBook(2);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.ACTIVE
        );

        when(reservationRepository.findById(RESERVATION_ID))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.cancelReservation(
                        RESERVATION_ID,
                        USER_EMAIL,
                        false
                );

        assertEquals(
                ReservationStatus.CANCELLED,
                response.getStatus()
        );
        assertNotNull(response.getCancelledAt());
        assertEquals(3, book.getAvailableCopies());

        verify(bookRepository).save(book);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldAllowAdminToCancelAnotherUsersReservation() {
        User user = createUser();
        Book book = createBook(2);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.ACTIVE
        );

        when(reservationRepository.findById(RESERVATION_ID))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.cancelReservation(
                        RESERVATION_ID,
                        "admin@bookstore.com",
                        true
                );

        assertEquals(
                ReservationStatus.CANCELLED,
                response.getStatus()
        );
        assertEquals(3, book.getAvailableCopies());
        assertNotNull(response.getCancelledAt());

        verify(bookRepository).save(book);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldRejectCancellingAnotherUsersReservation() {
        User user = createUser();
        Book book = createBook(2);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.ACTIVE
        );

        when(reservationRepository.findById(RESERVATION_ID))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                InvalidReservationOperationException.class,
                () -> reservationService.cancelReservation(
                        RESERVATION_ID,
                        "other@bookstore.com",
                        false
                )
        );

        assertEquals(
                ReservationStatus.ACTIVE,
                reservation.getStatus()
        );
        assertEquals(2, book.getAvailableCopies());

        verify(bookRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectCancellingCompletedReservation() {
        User user = createUser();
        Book book = createBook(2);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.COMPLETED
        );

        when(reservationRepository.findById(RESERVATION_ID))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                InvalidReservationOperationException.class,
                () -> reservationService.cancelReservation(
                        RESERVATION_ID,
                        USER_EMAIL,
                        false
                )
        );

        assertEquals(2, book.getAvailableCopies());

        verify(bookRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectCancellingAlreadyCancelledReservation() {
        User user = createUser();
        Book book = createBook(2);

        Reservation reservation = new Reservation(
                user,
                book,
                ReservationStatus.CANCELLED
        );

        when(reservationRepository.findById(RESERVATION_ID))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                InvalidReservationOperationException.class,
                () -> reservationService.cancelReservation(
                        RESERVATION_ID,
                        USER_EMAIL,
                        false
                )
        );

        verify(bookRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExist() {
        when(reservationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundException.class,
                () -> reservationService.cancelReservation(
                        99L,
                        USER_EMAIL,
                        false
                )
        );

        verify(bookRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    private ReservationRequest createRequest(Long bookId) {
        ReservationRequest request = new ReservationRequest();
        request.setBookId(bookId);
        return request;
    }

    private User createUser() {
        Role role = new Role("USER");

        return new User(
                USER_EMAIL,
                "encoded-password",
                "Jan",
                "Kowalski",
                role
        );
    }

    private Book createBook(int availableCopies) {
        return new Book(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "programming",
                "Book about clean code",
                availableCopies
        );
    }
}
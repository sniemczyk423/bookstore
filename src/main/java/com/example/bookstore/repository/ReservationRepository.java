package com.example.bookstore.repository;

import com.example.bookstore.entity.Reservation;
import com.example.bookstore.entity.ReservationStatus;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserOrderByReservedAtDesc(User user);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByUserAndStatus(User user, ReservationStatus status);

    boolean existsByUserAndBookIdAndStatus(
            User user,
            Long bookId,
            ReservationStatus status
    );
}
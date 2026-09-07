package com.oibsip.library.repository;

import com.oibsip.library.model.Reservation;
import com.oibsip.library.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser_IdOrderByReservationDateDesc(Long userId);

    Optional<Reservation> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByBook_IdAndUser_IdAndStatus(Long bookId, Long userId, ReservationStatus status);
}

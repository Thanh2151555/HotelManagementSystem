package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    // Find overlapping reservations for a given room (excluding cancelled and the reservation itself)
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.status <> 'CANCELLED' " +
            "AND (:checkIn < r.checkOutExpected AND :checkOut > r.checkInExpected)"
    )
    List<Reservation> findOverlapping(@Param("roomId") Integer roomId,
                                      @Param("checkIn") LocalDateTime checkIn,
                                      @Param("checkOut") LocalDateTime checkOut);

    long countByActualCheckInTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByActualCheckOutTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.roomType.id = :roomTypeId " +
            "AND r.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
            "AND (:checkIn < r.checkOutExpected AND :checkOut > r.checkInExpected)")
    long countOverlappingByType(@Param("roomTypeId") Integer roomTypeId,
                                @Param("checkIn") LocalDateTime checkIn,
                                @Param("checkOut") LocalDateTime checkOut);
    
    List<Reservation> findByGuestId(Integer guestId);
}

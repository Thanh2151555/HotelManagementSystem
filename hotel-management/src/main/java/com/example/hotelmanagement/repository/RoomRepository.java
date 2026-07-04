package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    Optional<Room> findByRoomNumber(String roomNumber);
    Optional<Room> findByIdAndStatus(Integer id, String status);
    // Find an AVAILABLE room for quick assignment
    Optional<Room> findFirstByStatus(String status);
    long countByStatus(String status);
    long countByRoomTypeId(Integer roomTypeId);
}

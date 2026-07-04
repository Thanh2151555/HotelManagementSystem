package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    
    @Query("SELECT g FROM Guest g WHERE " +
           "LOWER(g.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "g.identityNumber LIKE CONCAT('%', :keyword, '%') OR " +
           "g.phone LIKE CONCAT('%', :keyword, '%')")
    Page<Guest> searchGuests(@Param("keyword") String keyword, Pageable pageable);

    Optional<Guest> findByIdentityNumber(String identityNumber);
}

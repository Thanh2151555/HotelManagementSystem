package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.response.ReservationResponse;
import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.UserRepository;
import com.example.hotelmanagement.service.CustomerPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerPortalServiceImpl implements CustomerPortalService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReservationResponse> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Guest> guests = guestRepository.findAll();
        Guest customerGuest = null;
        for (Guest g : guests) {
            if (g.getUser() != null && g.getUser().getId().equals(user.getId())) {
                customerGuest = g;
                break;
            }
        }

        if (customerGuest == null) {
            return new ArrayList<>();
        }

        List<Reservation> reservations = reservationRepository.findByGuestId(customerGuest.getId());
        
        return reservations.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse cancelMyReservation(Integer reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getGuest().getUser() == null || !reservation.getGuest().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to cancel this reservation");
        }

        if (!"PENDING".equals(reservation.getStatus())) {
            throw new RuntimeException("Only PENDING reservations can be cancelled online. Please contact hotline.");
        }

        if (reservation.getDepositAmount() != null && reservation.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Reservation has a deposit. Please contact hotline to cancel.");
        }

        if (reservation.getCheckInExpected().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel within 2 hours of check-in time.");
        }

        reservation.setStatus("CANCELLED");
        reservation = reservationRepository.save(reservation);
        
        return mapToResponse(reservation);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .guestId(reservation.getGuest().getId())
                .guestName(reservation.getGuest().getFullName())
                .roomTypeId(reservation.getRoomType().getId())
                .roomTypeName(reservation.getRoomType().getTypeName())
                .roomId(reservation.getRoom() != null ? reservation.getRoom().getId() : null)
                .roomNumber(reservation.getRoom() != null ? reservation.getRoom().getRoomNumber() : null)
                .checkInExpected(reservation.getCheckInExpected())
                .checkOutExpected(reservation.getCheckOutExpected())
                .actualCheckInTime(reservation.getActualCheckInTime())
                .actualCheckOutTime(reservation.getActualCheckOutTime())
                .depositAmount(reservation.getDepositAmount())
                .status(reservation.getStatus())
                .bookingReference(reservation.getBookingReference())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}

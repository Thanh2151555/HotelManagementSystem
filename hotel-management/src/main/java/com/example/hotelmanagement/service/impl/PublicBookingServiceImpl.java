package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.PublicBookingRequest;
import com.example.hotelmanagement.dto.response.AvailableRoomTypeResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;
import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import com.example.hotelmanagement.repository.UserRepository;
import com.example.hotelmanagement.service.PublicBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicBookingServiceImpl implements PublicBookingService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    @Override
    public List<AvailableRoomTypeResponse> getAvailableRoomTypes(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkOut.isBefore(checkIn)) {
            throw new IllegalArgumentException("Check-out time must be after check-in time");
        }

        List<RoomType> allTypes = roomTypeRepository.findAll();
        List<AvailableRoomTypeResponse> response = new ArrayList<>();

        for (RoomType type : allTypes) {
            long totalRooms = roomRepository.countByRoomTypeId(type.getId());
            long overlapping = reservationRepository.countOverlappingByType(type.getId(), checkIn, checkOut);
            
            long availableCount = totalRooms - overlapping;
            if (availableCount > 0) {
                response.add(AvailableRoomTypeResponse.builder()
                        .roomTypeId(type.getId())
                        .typeName(type.getTypeName())
                        .defaultPrice(new BigDecimal("0")) // Missing defaultPrice in RoomType
                        .availableCount(availableCount)
                        .build());
            }
        }
        return response;
    }

    @Override
    @Transactional
    public ReservationResponse createPublicBooking(PublicBookingRequest request) {
        if (request.getCheckOutExpected().isBefore(request.getCheckInExpected())) {
            throw new IllegalArgumentException("Check-out time must be after check-in time");
        }

        // Check availability
        long totalRooms = roomRepository.countByRoomTypeId(request.getRoomTypeId());
        long overlapping = reservationRepository.countOverlappingByType(request.getRoomTypeId(), request.getCheckInExpected(), request.getCheckOutExpected());
        if (totalRooms - overlapping <= 0) {
            throw new IllegalStateException("Not enough rooms available for this type in the specified period");
        }

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        // Handle Guest
        Guest guest = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            // User is logged in
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                // Try to find guest by user
                List<Guest> guests = guestRepository.findAll();
                for (Guest g : guests) {
                    if (g.getUser() != null && g.getUser().getId().equals(user.getId())) {
                        guest = g;
                        break;
                    }
                }
            }
        }

        if (guest == null) {
            // Find by identity or create new
            guest = guestRepository.findByIdentityNumber(request.getIdentityNumber()).orElse(null);
            if (guest == null) {
                guest = Guest.builder()
                        .fullName(request.getFullName())
                        .identityNumber(request.getIdentityNumber())
                        .phone(request.getPhone())
                        .build();
                guest = guestRepository.save(guest);
            }
        }

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .roomType(roomType)
                .checkInExpected(request.getCheckInExpected())
                .checkOutExpected(request.getCheckOutExpected())
                .status("PENDING") // Initial status for online booking
                .depositAmount(BigDecimal.ZERO)
                .build();

        reservation = reservationRepository.save(reservation);

        return ReservationResponse.builder()
                .id(reservation.getId())
                .guestName(guest.getFullName())
                .roomTypeName(roomType.getTypeName())
                .checkInExpected(reservation.getCheckInExpected())
                .checkOutExpected(reservation.getCheckOutExpected())
                .status(reservation.getStatus())
                .depositAmount(reservation.getDepositAmount())
                // bookingReference is generated in prePersist, might need to wait for commit or we can fetch it
                .build();
    }
}

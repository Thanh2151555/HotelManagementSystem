package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.ReservationRequest;
import com.example.hotelmanagement.dto.request.AssignRoomRequest;
import com.example.hotelmanagement.dto.response.PageResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;
import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import com.example.hotelmanagement.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.dto.request.CheckInRequest;
import com.example.hotelmanagement.dto.response.CheckInResponse;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.repository.PricingRuleRepository;
import com.example.hotelmanagement.entity.PricingRule;
import com.example.hotelmanagement.dto.response.CheckOutResponse;
import com.example.hotelmanagement.enums.InvoiceStatus;
import java.time.Duration;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;
    private final PricingRuleRepository pricingRuleRepository;

    @Override
    public PageResponse<ReservationResponse> getAllReservations(int page, int size) {
        if (size > 50) size = 50;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        
        List<ReservationResponse> content = reservationPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<ReservationResponse>builder()
                .content(content)
                .pageNumber(reservationPage.getNumber())
                .pageSize(reservationPage.getSize())
                .totalElements(reservationPage.getTotalElements())
                .totalPages(reservationPage.getTotalPages())
                .isLast(reservationPage.isLast())
                .build();
    }

    @Override
    public ReservationResponse getReservationById(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToResponse(reservation);
    }

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        if (request.getCheckOutExpected().isBefore(request.getCheckInExpected())) {
            throw new RuntimeException("Check-out time must be after check-in time");
        }

        Guest guest = guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new RuntimeException("Guest not found"));
                
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
        }

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .roomType(roomType)
                .room(room)
                .checkInExpected(request.getCheckInExpected())
                .checkOutExpected(request.getCheckOutExpected())
                .depositAmount(request.getDepositAmount())
                .status("CONFIRMED")
                .build();

        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse assignRoom(AssignRoomRequest request) {
        // 1. Validate reservation existence
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        // 2. Ensure reservation is not cancelled and not already assigned a room
        if ("CANCELLED".equalsIgnoreCase(reservation.getStatus())) {
            throw new IllegalStateException("Cannot assign room to a cancelled reservation");
        }
        if (reservation.getRoom() != null) {
            throw new IllegalStateException("Room already assigned to this reservation");
        }
        // 3. Find target room and ensure it matches room type and is AVAILABLE
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getRoomType().getId().equals(reservation.getRoomType().getId())) {
            throw new IllegalStateException("Room type does not match reservation's room type");
        }
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Room is not available for assignment");
        }
        // 4. Check overlapping reservations for the room (excluding this reservation)
        boolean overlapExists = reservationRepository.findOverlapping(
                room.getId(), reservation.getCheckInExpected(), reservation.getCheckOutExpected())
                .stream()
                .anyMatch(r -> !r.getId().equals(reservation.getId()));
        if (overlapExists) {
            throw new IllegalStateException("Room is already booked for the requested period");
        }
        // 5. Assign room
        reservation.setRoom(room);
        reservationRepository.save(reservation);
        roomRepository.save(room);
        return mapToResponse(reservation);
    }

    @Override
    @Transactional
    public CheckInResponse checkIn(CheckInRequest request) {
        // 1. Validate reservation existence and status
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (!"CONFIRMED".equalsIgnoreCase(reservation.getStatus())) {
            throw new IllegalStateException("Only CONFIRMED reservations can be checked in");
        }
        // 2. Handle room assignment based on business rules
        Room room = reservation.getRoom();
        if (room != null) {
            // Reservation already has a room; ensure no change unless explicitly allowed elsewhere
            if (request.getRoomId() != null && !request.getRoomId().equals(room.getId())) {
                throw new IllegalStateException("Reservation already has a room assigned; cannot change here");
            }
        } else {
            // No room assigned yet, require roomId in request
            Integer roomId = request.getRoomId();
            if (roomId == null) {
                throw new IllegalArgumentException("roomId is required for reservations without a pre‑assigned room");
            }
            room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            // Validate room type matches reservation
            if (!room.getRoomType().getId().equals(reservation.getRoomType().getId())) {
                throw new IllegalStateException("Room type does not match reservation's room type");
            }
            // Check overlapping reservations for this room
            boolean overlapExists = reservationRepository.findOverlapping(
                    room.getId(), reservation.getCheckInExpected(), reservation.getCheckOutExpected())
                    .stream()
                    .anyMatch(r -> !r.getId().equals(reservation.getId()));
            if (overlapExists) {
                throw new IllegalStateException("Room is already booked for the reservation period");
            }
            // Assign room
            reservation.setRoom(room);
        }

        // Validate room is physically AVAILABLE for check-in
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Room is not AVAILABLE for check-in");
        }

        // Update room status to OCCUPIED
        room.setStatus("OCCUPIED");
        roomRepository.save(room);

        // 3. Update deposit if applicable
        if (reservation.getDepositAmount() == null || reservation.getDepositAmount().compareTo(BigDecimal.ZERO) == 0) {
            if (request.getDepositPaid() != null) {
                reservation.setDepositAmount(request.getDepositPaid());
            }
        }
        
        // Save rental type
        reservation.setRentalType(request.getRentalType());

        // 4. Update statuses and timestamps
        reservation.setStatus("CHECKED_IN");
        reservation.setActualCheckInTime(LocalDateTime.now());
        room.setStatus("OCCUPIED");
        // 5. Persist changes
        reservationRepository.save(reservation);
        roomRepository.save(room);
        // 6. Create Invoice (UNPAID)
        Invoice invoice = Invoice.builder()
                .reservation(reservation)
                .roomCharge(BigDecimal.ZERO)
                .serviceCharge(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .amountPaid(BigDecimal.ZERO)
                .amountDue(BigDecimal.ZERO.subtract(reservation.getDepositAmount() != null ? reservation.getDepositAmount() : BigDecimal.ZERO))
                .status(InvoiceStatus.UNPAID)
                .createdAt(LocalDateTime.now())
                .build();
        invoiceRepository.save(invoice);
        // 7. Build response
        CheckInResponse response = new CheckInResponse();
        response.setReservationId(reservation.getId());
        response.setRoomId(room.getId());
        response.setActualCheckInTime(reservation.getActualCheckInTime());
        response.setReservationStatus(reservation.getStatus());
        response.setRoomStatus(room.getStatus());
        return response;
    }
    @Override
    @Transactional
    public ReservationResponse confirmReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!"PENDING".equals(reservation.getStatus())) {
            throw new IllegalStateException("Only PENDING reservations can be confirmed");
        }

        reservation.setStatus("CONFIRMED");
        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    @Override
    public ReservationResponse cancelReservation(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!"CONFIRMED".equals(reservation.getStatus())) {
            throw new RuntimeException("Only CONFIRMED reservations can be cancelled");
        }

        reservation.setStatus("CANCELLED");
        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    @Override
    @Transactional
    public CheckOutResponse checkOut(Integer reservationId) {
        // 1. Validate reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!"CHECKED_IN".equalsIgnoreCase(reservation.getStatus())) {
            throw new IllegalStateException("Only CHECKED_IN reservations can be checked out");
        }

        // 2. Validate invoice
        Invoice invoice = invoiceRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for this reservation"));

        // 3. Update checkout time
        LocalDateTime now = LocalDateTime.now();
        reservation.setActualCheckOutTime(now);

        // 4. Calculate room charge
        BigDecimal roomCharge = calculateRoomCharge(reservation, now);

        // 5. Update Invoice
        invoice.setRoomCharge(roomCharge);
        BigDecimal serviceCharge = invoice.getServiceCharge() != null ? invoice.getServiceCharge() : BigDecimal.ZERO;
        BigDecimal totalAmount = roomCharge.add(serviceCharge);
        invoice.setTotalAmount(totalAmount);
        
        BigDecimal deposit = reservation.getDepositAmount() != null ? reservation.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal amountDue = totalAmount.subtract(deposit);
        // Ensure amount due is not negative (if deposit > total, maybe refund, but keep simple for now)
        invoice.setAmountDue(amountDue);
        
        invoiceRepository.save(invoice);

        // 6. Update statuses
        reservation.setStatus("CHECKED_OUT");
        reservationRepository.save(reservation);

        if (reservation.getRoom() != null) {
            Room room = reservation.getRoom();
            room.setStatus("DIRTY"); // using String since enum migration is deferred
            roomRepository.save(room);
        }

        // 7. Build Response
        CheckOutResponse response = new CheckOutResponse();
        response.setReservationId(reservation.getId());
        response.setActualCheckOutTime(reservation.getActualCheckOutTime());
        response.setRoomCharge(invoice.getRoomCharge());
        response.setServiceCharge(invoice.getServiceCharge());
        response.setDepositAmount(deposit);
        response.setTotalAmount(invoice.getTotalAmount());
        response.setAmountDue(invoice.getAmountDue());
        response.setInvoiceStatus(invoice.getStatus().name());
        response.setReservationStatus(reservation.getStatus());
        response.setRoomStatus(reservation.getRoom() != null ? reservation.getRoom().getStatus() : null);

        return response;
    }

    private BigDecimal calculateRoomCharge(Reservation reservation, LocalDateTime checkOutTime) {
        String rentalType = reservation.getRentalType();
        if (rentalType == null) {
            rentalType = "DAILY"; // fallback
        }
        
        LocalDateTime checkInTime = reservation.getActualCheckInTime();
        if (checkInTime == null) {
            checkInTime = reservation.getCheckInExpected();
        }

        long hours = Duration.between(checkInTime, checkOutTime).toHours();
        // If minutes > 0, consider it as an extra hour (ceiling)
        if (Duration.between(checkInTime, checkOutTime).toMinutesPart() > 0) {
            hours++;
        }
        if (hours <= 0) hours = 1;

        Integer roomTypeId = reservation.getRoomType().getId();

        if ("HOURLY".equalsIgnoreCase(rentalType)) {
            // Find HOURLY_FIRST and HOURLY_NEXT rules
            PricingRule ruleFirst = pricingRuleRepository.findByRoomTypeIdAndRentalTypeAndIsActiveTrue(roomTypeId, "HOURLY_FIRST")
                    .orElseThrow(() -> new RuntimeException("Pricing rule HOURLY_FIRST not found"));
            PricingRule ruleNext = pricingRuleRepository.findByRoomTypeIdAndRentalTypeAndIsActiveTrue(roomTypeId, "HOURLY_NEXT")
                    .orElse(null);

            BigDecimal total = ruleFirst.getPrice();
            if (hours > 1 && ruleNext != null) {
                total = total.add(ruleNext.getPrice().multiply(BigDecimal.valueOf(hours - 1)));
            }
            return total;
        } else if ("OVERNIGHT".equalsIgnoreCase(rentalType)) {
            PricingRule ruleOvernight = pricingRuleRepository.findByRoomTypeIdAndRentalTypeAndIsActiveTrue(roomTypeId, "OVERNIGHT")
                    .orElseThrow(() -> new RuntimeException("Pricing rule OVERNIGHT not found"));
            // Simple assumption: 1 overnight charge
            return ruleOvernight.getPrice();
        } else {
            // DAILY
            PricingRule ruleDaily = pricingRuleRepository.findByRoomTypeIdAndRentalTypeAndIsActiveTrue(roomTypeId, "DAILY")
                    .orElseThrow(() -> new RuntimeException("Pricing rule DAILY not found"));
            long days = hours / 24;
            if (hours % 24 > 0) days++;
            if (days <= 0) days = 1;
            return ruleDaily.getPrice().multiply(BigDecimal.valueOf(days));
        }
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

package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room; // Có thể null nếu chỉ đặt loại phòng

    @Column(name = "check_in_expected", nullable = false)
    private LocalDateTime checkInExpected;

    @Column(name = "check_out_expected", nullable = false)
    private LocalDateTime checkOutExpected;

    @Column(name = "actual_check_in_time")
    private LocalDateTime actualCheckInTime;

    @Column(name = "actual_check_out_time")
    private LocalDateTime actualCheckOutTime;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "rental_type", length = 20)
    private String rentalType; // HOURLY, OVERNIGHT, DAILY

    @Column(length = 20)
    private String status; // PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED

    @Column(name = "booking_reference", length = 20, unique = true)
    private String bookingReference;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "CONFIRMED";
        if (depositAmount == null) depositAmount = BigDecimal.ZERO;
        if (bookingReference == null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
            String dateStr = LocalDateTime.now().format(formatter);
            String randomStr = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            this.bookingReference = "BK" + dateStr + randomStr;
        }
    }
}

package com.example.hotelmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResponse {
    private Integer id;
    private Integer guestId;
    private String guestName;
    private Integer roomTypeId;
    private String roomTypeName;
    private Integer roomId;
    private String roomNumber;
    private LocalDateTime checkInExpected;
    private LocalDateTime checkOutExpected;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private BigDecimal depositAmount;
    private String status;
    private String bookingReference;
    private LocalDateTime createdAt;
}

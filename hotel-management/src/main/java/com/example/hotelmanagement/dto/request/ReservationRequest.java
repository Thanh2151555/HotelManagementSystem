package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    @NotNull(message = "Guest ID is required")
    private Integer guestId;

    @NotNull(message = "Room Type ID is required")
    private Integer roomTypeId;

    private Integer roomId; // Có thể null

    @NotNull(message = "Expected check-in time is required")
    private LocalDateTime checkInExpected;

    @NotNull(message = "Expected check-out time is required")
    private LocalDateTime checkOutExpected;

    private BigDecimal depositAmount;
}

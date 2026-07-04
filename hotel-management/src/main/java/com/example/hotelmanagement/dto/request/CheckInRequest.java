package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CheckInRequest {
    @NotNull(message = "reservationId is required")
    private Integer reservationId;
    // optional room assignment if not already assigned
    private Integer roomId;
    // optional deposit amount paid at check‑in (override existing if provided)
    private BigDecimal depositPaid;
    
    @NotNull(message = "rentalType is required (HOURLY, OVERNIGHT, DAILY)")
    private String rentalType;

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public BigDecimal getDepositPaid() { return depositPaid; }
    public void setDepositPaid(BigDecimal depositPaid) { this.depositPaid = depositPaid; }
    public String getRentalType() { return rentalType; }
    public void setRentalType(String rentalType) { this.rentalType = rentalType; }
}

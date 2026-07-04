package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignRoomRequest {
    @NotNull(message = "reservationId is required")
    private Integer reservationId;

    @NotNull(message = "roomId is required")
    private Integer roomId;

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
}

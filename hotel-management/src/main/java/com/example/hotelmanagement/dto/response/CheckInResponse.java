package com.example.hotelmanagement.dto.response;

import java.time.LocalDateTime;

public class CheckInResponse {
    private Integer reservationId;
    private Integer roomId;
    private LocalDateTime actualCheckInTime;
    private String reservationStatus;
    private String roomStatus;

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public LocalDateTime getActualCheckInTime() { return actualCheckInTime; }
    public void setActualCheckInTime(LocalDateTime actualCheckInTime) { this.actualCheckInTime = actualCheckInTime; }
    public String getReservationStatus() { return reservationStatus; }
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }
    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }
}

package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.PublicBookingRequest;
import com.example.hotelmanagement.dto.response.AvailableRoomTypeResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicBookingService {
    List<AvailableRoomTypeResponse> getAvailableRoomTypes(LocalDateTime checkIn, LocalDateTime checkOut);
    ReservationResponse createPublicBooking(PublicBookingRequest request);
}

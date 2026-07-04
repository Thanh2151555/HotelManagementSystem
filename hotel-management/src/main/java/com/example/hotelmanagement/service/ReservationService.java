package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.AssignRoomRequest;
import com.example.hotelmanagement.dto.request.ReservationRequest;
import com.example.hotelmanagement.dto.request.CheckInRequest;
import com.example.hotelmanagement.dto.response.CheckInResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse confirmReservation(Integer reservationId);
    ReservationResponse cancelReservation(Integer reservationId);
    ReservationResponse getReservationById(Integer reservationId);
    com.example.hotelmanagement.dto.response.PageResponse<ReservationResponse> getAllReservations(int page, int size);
    // Sprint 7: Gán phòng và khóa phòng tránh overbooking
    ReservationResponse assignRoom(AssignRoomRequest request);
    // Sprint 8: Check‑in
    CheckInResponse checkIn(CheckInRequest request);
    // Sprint 9: Check‑out
    com.example.hotelmanagement.dto.response.CheckOutResponse checkOut(Integer reservationId);
}

package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.response.ReservationResponse;

import java.util.List;

public interface CustomerPortalService {
    List<ReservationResponse> getMyReservations();
    ReservationResponse cancelMyReservation(Integer reservationId);
}

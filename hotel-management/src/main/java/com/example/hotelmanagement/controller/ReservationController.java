package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.ReservationRequest;
import com.example.hotelmanagement.dto.request.AssignRoomRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.PageResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;
import com.example.hotelmanagement.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return ResponseEntity.ok(ApiResponse.success(reservationService.getAllReservations(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getReservationById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Reservation created successfully", reservationService.createReservation(request)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(@PathVariable Integer id) {
        ReservationResponse response = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation cancelled successfully", response));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmReservation(@PathVariable Integer id) {
        ReservationResponse response = reservationService.confirmReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation confirmed successfully", response));
    }

    @PostMapping("/assign-room")
    public ResponseEntity<ApiResponse<ReservationResponse>> assignRoom(@Valid @RequestBody AssignRoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.assignRoom(request)));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<com.example.hotelmanagement.dto.response.CheckInResponse>> checkIn(@Valid @RequestBody com.example.hotelmanagement.dto.request.CheckInRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.checkIn(request)));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<com.example.hotelmanagement.dto.response.CheckOutResponse>> checkOut(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.checkOut(id)));
    }
}

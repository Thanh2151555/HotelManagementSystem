package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.PublicBookingRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.AvailableRoomTypeResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;
import com.example.hotelmanagement.service.PublicBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicBookingController {

    private final PublicBookingService publicBookingService;

    @GetMapping("/room-types/available")
    public ResponseEntity<ApiResponse<List<AvailableRoomTypeResponse>>> getAvailableRoomTypes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        
        List<AvailableRoomTypeResponse> response = publicBookingService.getAvailableRoomTypes(checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/booking")
    public ResponseEntity<ApiResponse<ReservationResponse>> createBooking(@Valid @RequestBody PublicBookingRequest request) {
        ReservationResponse response = publicBookingService.createPublicBooking(request);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", response));
    }
}

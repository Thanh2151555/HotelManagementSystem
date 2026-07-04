package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.ReservationResponse;
import com.example.hotelmanagement.service.CustomerPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerPortalController {

    private final CustomerPortalService customerPortalService;

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations() {
        List<ReservationResponse> response = customerPortalService.getMyReservations();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/reservations/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelMyReservation(@PathVariable Integer id) {
        ReservationResponse response = customerPortalService.cancelMyReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation cancelled successfully", response));
    }
}

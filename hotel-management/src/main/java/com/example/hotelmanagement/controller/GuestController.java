package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.GuestRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.GuestResponse;
import com.example.hotelmanagement.dto.response.PageResponse;
import com.example.hotelmanagement.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GuestResponse>>> searchGuests(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return ResponseEntity.ok(ApiResponse.success(guestService.searchGuests(keyword, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GuestResponse>> getGuestById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(guestService.getGuestById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GuestResponse>> createGuest(@Valid @RequestBody GuestRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created successfully", guestService.createGuest(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GuestResponse>> updateGuest(@PathVariable Integer id, @Valid @RequestBody GuestRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", guestService.updateGuest(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGuest(@PathVariable Integer id) {
        guestService.deleteGuest(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}

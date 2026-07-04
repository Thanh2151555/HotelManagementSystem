package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.RoomRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.RoomResponse;
import com.example.hotelmanagement.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created successfully", roomService.createRoom(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", roomService.updateRoom(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    // API đặc biệt để thay đổi trạng thái nhanh (Dọn phòng, Bảo trì...)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new RuntimeException("Status is required");
        }
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", roomService.updateRoomStatus(id, status)));
    }

    @PutMapping("/{id}/complete-cleaning")
    public ResponseEntity<ApiResponse<RoomResponse>> completeCleaning(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Cleaning completed successfully", roomService.completeCleaning(id)));
    }
}

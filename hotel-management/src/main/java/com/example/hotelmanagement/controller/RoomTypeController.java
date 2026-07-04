package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.RoomTypeRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.RoomTypeResponse;
import com.example.hotelmanagement.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // trả về kiểu dữ k
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomTypeResponse>>> getAllRoomTypes() {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getAllRoomTypes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> getRoomTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getRoomTypeById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomTypeResponse>> createRoomType(@Valid @RequestBody RoomTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created successfully", roomTypeService.createRoomType(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> updateRoomType(@PathVariable Integer id, @Valid @RequestBody RoomTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", roomTypeService.updateRoomType(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRoomType(@PathVariable Integer id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}

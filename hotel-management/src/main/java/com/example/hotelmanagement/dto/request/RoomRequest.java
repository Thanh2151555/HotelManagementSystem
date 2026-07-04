package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {
    @NotNull(message = "Room Type ID is required")
    private Integer roomTypeId;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    private String status; // Optional for creation, defaults to AVAILABLE
}

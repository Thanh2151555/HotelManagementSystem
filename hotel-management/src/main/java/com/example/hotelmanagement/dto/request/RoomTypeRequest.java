package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomTypeRequest {
    @NotBlank(message = "Type name is required")
    private String typeName;
}

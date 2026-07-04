package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicBookingRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Identity number is required")
    private String identityNumber;

    @NotNull(message = "Room Type ID is required")
    private Integer roomTypeId;

    @NotNull(message = "Expected Check-In time is required")
    private LocalDateTime checkInExpected;

    @NotNull(message = "Expected Check-Out time is required")
    private LocalDateTime checkOutExpected;
}

package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuestRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Identity number (CMND/CCCD) is required")
    private String identityNumber;

    private String phone;
    
    private Integer userId; // Optional
}

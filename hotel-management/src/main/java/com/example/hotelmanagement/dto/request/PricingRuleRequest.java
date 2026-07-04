package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PricingRuleRequest {
    @NotNull(message = "Room Type ID is required")
    private Integer roomTypeId;

    @NotBlank(message = "Rental type is required (e.g. HOURLY_FIRST, HOURLY_NEXT, OVERNIGHT, DAILY)")
    private String rentalType;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    private Boolean isSeasonal;
    private LocalDateTime effectiveDate;
    private Boolean isActive;
}

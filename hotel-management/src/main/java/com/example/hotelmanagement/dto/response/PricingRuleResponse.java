package com.example.hotelmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PricingRuleResponse {
    private Integer id;
    private Integer roomTypeId;
    private String roomTypeName;
    private String rentalType;
    private BigDecimal price;
    private Boolean isSeasonal;
    private LocalDateTime effectiveDate;
    private Boolean isActive;
}

package com.example.hotelmanagement.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRevenue {
    private String date; // Can be formatted string like "2026-07-01" or just "01/07"
    private BigDecimal amount;
}

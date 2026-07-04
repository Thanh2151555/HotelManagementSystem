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
public class RoomStatusReportItem {
    private String status;
    private long count;
    private BigDecimal percentage;
}

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
public class DashboardSummaryResponse {
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long dirtyRooms;
    private long maintenanceRooms;
    private long todayCheckIns;
    private long todayCheckOuts;
    private BigDecimal todayRevenue;
}

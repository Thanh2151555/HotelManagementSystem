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
public class ServiceReportItem {
    private String serviceName;
    private long quantity;
    private BigDecimal totalAmount;
}

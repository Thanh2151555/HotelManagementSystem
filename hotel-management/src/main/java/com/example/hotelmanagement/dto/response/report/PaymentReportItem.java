package com.example.hotelmanagement.dto.response.report;

import com.example.hotelmanagement.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReportItem {
    private PaymentMethod paymentMethod;
    private long count;
    private BigDecimal amount;
}

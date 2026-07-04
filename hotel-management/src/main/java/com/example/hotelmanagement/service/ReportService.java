package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.response.report.OccupancyReportResponse;
import com.example.hotelmanagement.dto.response.report.PaymentReportResponse;
import com.example.hotelmanagement.dto.response.report.RevenueReportResponse;
import com.example.hotelmanagement.dto.response.report.RoomStatusReportResponse;
import com.example.hotelmanagement.dto.response.report.ServiceReportResponse;

import java.time.LocalDate;

public interface ReportService {
    RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate);
    OccupancyReportResponse getOccupancyReport(LocalDate startDate, LocalDate endDate);
    ServiceReportResponse getServiceReport(LocalDate startDate, LocalDate endDate);
    PaymentReportResponse getPaymentReport(LocalDate startDate, LocalDate endDate);
    RoomStatusReportResponse getRoomStatusReport();
}

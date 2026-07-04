package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.report.*;
import com.example.hotelmanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getRevenueReport(startDate, endDate)));
    }

    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<OccupancyReportResponse>> getOccupancyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getOccupancyReport(startDate, endDate)));
    }

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<ServiceReportResponse>> getServiceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getServiceReport(startDate, endDate)));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentReportResponse>> getPaymentReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getPaymentReport(startDate, endDate)));
    }

    @GetMapping("/room-status")
    public ResponseEntity<ApiResponse<RoomStatusReportResponse>> getRoomStatusReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getRoomStatusReport()));
    }
}

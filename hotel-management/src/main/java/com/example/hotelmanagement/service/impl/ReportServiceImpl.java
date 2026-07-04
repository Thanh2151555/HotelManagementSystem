package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.response.report.*;
import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.repository.PaymentRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.ServiceOrderRepository;
import com.example.hotelmanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    @Override
    public RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<Object[]> results = paymentRepository.getRevenueByDateBetween(start, end);
        List<DailyRevenue> data = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Object[] result : results) {
            String date = result[0] != null ? result[0].toString() : "Unknown";
            BigDecimal amount = result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO;
            data.add(new DailyRevenue(date, amount));
            grandTotal = grandTotal.add(amount);
        }

        return RevenueReportResponse.builder()
                .data(data)
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    public OccupancyReportResponse getOccupancyReport(LocalDate startDate, LocalDate endDate) {
        // MVP: Simple current occupancy, ignoring the dates for now
        // A real occupancy report over a period requires tracking RoomNights.
        // For now, we will return the current occupancy rate to satisfy MVP.
        long totalRooms = roomRepository.count();
        long occupiedRooms = roomRepository.countByStatus("OCCUPIED");
        BigDecimal occupancyRate = BigDecimal.ZERO;

        if (totalRooms > 0) {
            occupancyRate = BigDecimal.valueOf(occupiedRooms)
                    .divide(BigDecimal.valueOf(totalRooms), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return OccupancyReportResponse.builder()
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .occupancyRate(occupancyRate)
                .build();
    }

    @Override
    public ServiceReportResponse getServiceReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<Object[]> results = serviceOrderRepository.getServiceReportBetween(start, end);
        List<ServiceReportItem> data = new ArrayList<>();

        for (Object[] result : results) {
            String name = (String) result[0];
            long quantity = result[1] != null ? ((Number) result[1]).longValue() : 0;
            BigDecimal amount = result[2] != null ? new BigDecimal(result[2].toString()) : BigDecimal.ZERO;
            data.add(new ServiceReportItem(name, quantity, amount));
        }

        return ServiceReportResponse.builder()
                .data(data)
                .build();
    }

    @Override
    public PaymentReportResponse getPaymentReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<Object[]> results = paymentRepository.getPaymentReportBetween(start, end);
        List<PaymentReportItem> data = new ArrayList<>();

        for (Object[] result : results) {
            PaymentMethod method = (PaymentMethod) result[0];
            long count = result[1] != null ? ((Number) result[1]).longValue() : 0;
            BigDecimal amount = result[2] != null ? new BigDecimal(result[2].toString()) : BigDecimal.ZERO;
            data.add(new PaymentReportItem(method, count, amount));
        }

        return PaymentReportResponse.builder()
                .data(data)
                .build();
    }

    @Override
    public RoomStatusReportResponse getRoomStatusReport() {
        long totalRooms = roomRepository.count();
        List<RoomStatusReportItem> data = new ArrayList<>();

        String[] statuses = {"AVAILABLE", "OCCUPIED", "DIRTY", "MAINTENANCE"};
        for (String status : statuses) {
            long count = roomRepository.countByStatus(status);
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalRooms > 0) {
                percentage = BigDecimal.valueOf(count)
                        .divide(BigDecimal.valueOf(totalRooms), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            data.add(new RoomStatusReportItem(status, count, percentage));
        }

        return RoomStatusReportResponse.builder()
                .data(data)
                .build();
    }
}

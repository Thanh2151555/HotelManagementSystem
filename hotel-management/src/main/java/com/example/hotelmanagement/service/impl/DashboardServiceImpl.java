package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.response.report.DashboardSummaryResponse;
import com.example.hotelmanagement.repository.PaymentRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByStatus("AVAILABLE");
        long occupiedRooms = roomRepository.countByStatus("OCCUPIED");
        long dirtyRooms = roomRepository.countByStatus("DIRTY");
        long maintenanceRooms = roomRepository.countByStatus("MAINTENANCE");

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);

        long todayCheckIns = reservationRepository.countByActualCheckInTimeBetween(startOfDay, endOfDay);
        long todayCheckOuts = reservationRepository.countByActualCheckOutTimeBetween(startOfDay, endOfDay);

        BigDecimal todayRevenue = paymentRepository.sumAmountByPaymentTimeBetween(startOfDay, endOfDay);
        if (todayRevenue == null) {
            todayRevenue = BigDecimal.ZERO;
        }

        return DashboardSummaryResponse.builder()
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .occupiedRooms(occupiedRooms)
                .dirtyRooms(dirtyRooms)
                .maintenanceRooms(maintenanceRooms)
                .todayCheckIns(todayCheckIns)
                .todayCheckOuts(todayCheckOuts)
                .todayRevenue(todayRevenue)
                .build();
    }
}

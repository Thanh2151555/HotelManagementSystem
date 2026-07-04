package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByInvoiceId(Integer invoiceId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentTime BETWEEN :start AND :end")
    java.math.BigDecimal sumAmountByPaymentTimeBetween(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);

    @org.springframework.data.jpa.repository.Query("SELECT FUNCTION('DATE', p.paymentTime), SUM(p.amount) FROM Payment p WHERE p.paymentTime BETWEEN :start AND :end GROUP BY FUNCTION('DATE', p.paymentTime)")
    List<Object[]> getRevenueByDateBetween(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);

    @org.springframework.data.jpa.repository.Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.paymentTime BETWEEN :start AND :end GROUP BY p.paymentMethod")
    List<Object[]> getPaymentReportBetween(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);
}

package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Integer> {
    List<ServiceOrder> findByInvoiceId(Integer invoiceId);

    @org.springframework.data.jpa.repository.Query("SELECT s.name, SUM(so.quantity), SUM(so.totalAmount) FROM ServiceOrder so JOIN so.service s WHERE so.orderTime BETWEEN :start AND :end GROUP BY s.name ORDER BY SUM(so.quantity) DESC")
    List<Object[]> getServiceReportBetween(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);
}

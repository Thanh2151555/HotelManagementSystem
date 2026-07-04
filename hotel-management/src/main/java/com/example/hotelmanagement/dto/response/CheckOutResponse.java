package com.example.hotelmanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CheckOutResponse {
    private Integer reservationId;
    private LocalDateTime actualCheckOutTime;
    private BigDecimal roomCharge;
    private BigDecimal serviceCharge;
    private BigDecimal depositAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountDue;
    private String invoiceStatus;
    private String reservationStatus;
    private String roomStatus;

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public LocalDateTime getActualCheckOutTime() { return actualCheckOutTime; }
    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) { this.actualCheckOutTime = actualCheckOutTime; }
    public BigDecimal getRoomCharge() { return roomCharge; }
    public void setRoomCharge(BigDecimal roomCharge) { this.roomCharge = roomCharge; }
    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }
    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
    public String getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    public String getReservationStatus() { return reservationStatus; }
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }
    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }
}

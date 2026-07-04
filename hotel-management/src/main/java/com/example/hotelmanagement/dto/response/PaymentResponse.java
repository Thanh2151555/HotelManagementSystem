package com.example.hotelmanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Integer paymentId;
    private Integer invoiceId;
    private String paymentMethod;
    private BigDecimal paymentAmount;
    private String invoiceStatus;
    private BigDecimal amountPaid;
    private BigDecimal amountDue;
    private LocalDateTime paymentTime;
    private String paymentReference;
    private String note;
    private String receivedByUsername;

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
    public String getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getReceivedByUsername() { return receivedByUsername; }
    public void setReceivedByUsername(String receivedByUsername) { this.receivedByUsername = receivedByUsername; }
}

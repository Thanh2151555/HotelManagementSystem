package com.example.hotelmanagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "invoiceId is required")
    private Integer invoiceId;

    @NotNull(message = "paymentMethod is required")
    private String paymentMethod; // CASH, BANK_TRANSFER, QR_CODE

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private Integer receivedById; // Optional for now, usually derived from logged in user

    private String paymentReference;

    private String note;

    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getReceivedById() { return receivedById; }
    public void setReceivedById(Integer receivedById) { this.receivedById = receivedById; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

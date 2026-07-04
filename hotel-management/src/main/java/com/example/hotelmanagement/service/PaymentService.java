package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.PaymentRequest;
import com.example.hotelmanagement.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    List<PaymentResponse> getPaymentsByInvoiceId(Integer invoiceId);
    org.springframework.data.domain.Page<PaymentResponse> getAllPayments(int page, int size);
}

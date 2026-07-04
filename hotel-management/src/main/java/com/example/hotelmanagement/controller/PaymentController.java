package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.PaymentRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.PaymentResponse;
import com.example.hotelmanagement.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", paymentService.processPayment(request)));
    }

    @GetMapping("/invoices/{id}/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByInvoiceId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByInvoiceId(id)));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments(page, size)));
    }
}

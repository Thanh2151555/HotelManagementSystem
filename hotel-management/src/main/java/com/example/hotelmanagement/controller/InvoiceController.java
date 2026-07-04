package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.ServiceOrderRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.ServiceOrderResponse;
import com.example.hotelmanagement.service.ServiceOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping("/{id}/services")
    public ResponseEntity<ApiResponse<ServiceOrderResponse>> addServiceToInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service added successfully", serviceOrderService.createServiceOrder(id, request)));
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<ApiResponse<List<ServiceOrderResponse>>> getServicesByInvoice(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(serviceOrderService.getServiceOrdersByInvoiceId(id)));
    }
}

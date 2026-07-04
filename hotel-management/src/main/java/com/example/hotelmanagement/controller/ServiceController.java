package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.ServiceRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.ServiceResponse;
import com.example.hotelmanagement.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.success(serviceCatalogService.getAllServices()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service created successfully", serviceCatalogService.createService(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(@PathVariable Integer id, @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service updated successfully", serviceCatalogService.updateService(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Integer id) {
        serviceCatalogService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted successfully", null));
    }
}

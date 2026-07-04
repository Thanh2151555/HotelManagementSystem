package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.ServiceRequest;
import com.example.hotelmanagement.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceCatalogService {
    List<ServiceResponse> getAllServices();
    ServiceResponse createService(ServiceRequest request);
    ServiceResponse updateService(Integer id, ServiceRequest request);
    void deleteService(Integer id);
}

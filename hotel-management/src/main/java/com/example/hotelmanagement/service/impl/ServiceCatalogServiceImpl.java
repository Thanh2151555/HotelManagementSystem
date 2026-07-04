package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.ServiceRequest;
import com.example.hotelmanagement.dto.response.ServiceResponse;
import com.example.hotelmanagement.entity.Service;
import com.example.hotelmanagement.repository.ServiceRepository;
import com.example.hotelmanagement.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse createService(ServiceRequest request) {
        Service service = Service.builder()
                .name(request.getName())
                .price(request.getPrice())
                .unit(request.getUnit())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        service = serviceRepository.save(service);
        return mapToResponse(service);
    }

    @Override
    public ServiceResponse updateService(Integer id, ServiceRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setUnit(request.getUnit());
        if (request.getDisplayOrder() != null) {
            service.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsActive() != null) {
            service.setIsActive(request.getIsActive());
        }

        service = serviceRepository.save(service);
        return mapToResponse(service);
    }

    @Override
    public void deleteService(Integer id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        // Soft delete
        service.setIsActive(false);
        serviceRepository.save(service);
    }

    private ServiceResponse mapToResponse(Service service) {
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        response.setPrice(service.getPrice());
        response.setUnit(service.getUnit());
        response.setDisplayOrder(service.getDisplayOrder());
        response.setIsActive(service.getIsActive());
        return response;
    }
}

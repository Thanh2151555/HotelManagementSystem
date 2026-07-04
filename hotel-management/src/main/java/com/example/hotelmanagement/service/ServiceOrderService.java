package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.ServiceOrderRequest;
import com.example.hotelmanagement.dto.response.ServiceOrderResponse;

import java.util.List;

public interface ServiceOrderService {
    ServiceOrderResponse createServiceOrder(Integer invoiceId, ServiceOrderRequest request);
    List<ServiceOrderResponse> getServiceOrdersByInvoiceId(Integer invoiceId);
}

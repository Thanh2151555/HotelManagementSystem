package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.ServiceOrderRequest;
import com.example.hotelmanagement.dto.response.ServiceOrderResponse;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Service;
import com.example.hotelmanagement.entity.ServiceOrder;
import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.enums.InvoiceStatus;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.ServiceOrderRepository;
import com.example.hotelmanagement.repository.ServiceRepository;
import com.example.hotelmanagement.repository.UserRepository;
import com.example.hotelmanagement.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ServiceOrderResponse createServiceOrder(Integer invoiceId, ServiceOrderRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.VOID) {
            throw new IllegalStateException("Cannot add service to a PAID or VOID invoice");
        }

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getIsActive()) {
            throw new IllegalStateException("Service is not active");
        }

        User createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        BigDecimal price = service.getPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(request.getQuantity()));

        ServiceOrder order = ServiceOrder.builder()
                .invoice(invoice)
                .service(service)
                .quantity(request.getQuantity())
                .price(price)
                .totalAmount(totalAmount)
                .createdBy(createdBy)
                .build();

        order = serviceOrderRepository.save(order);

        // Update Invoice Totals
        BigDecimal currentServiceCharge = invoice.getServiceCharge() != null ? invoice.getServiceCharge() : BigDecimal.ZERO;
        invoice.setServiceCharge(currentServiceCharge.add(totalAmount));
        
        BigDecimal currentTotal = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
        invoice.setTotalAmount(currentTotal.add(totalAmount));
        
        BigDecimal currentDue = invoice.getAmountDue() != null ? invoice.getAmountDue() : BigDecimal.ZERO;
        invoice.setAmountDue(currentDue.add(totalAmount));

        invoiceRepository.save(invoice);

        return mapToResponse(order);
    }

    @Override
    public List<ServiceOrderResponse> getServiceOrdersByInvoiceId(Integer invoiceId) {
        // verify invoice exists
        invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return serviceOrderRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ServiceOrderResponse mapToResponse(ServiceOrder order) {
        ServiceOrderResponse response = new ServiceOrderResponse();
        response.setId(order.getId());
        response.setInvoiceId(order.getInvoice().getId());
        response.setServiceId(order.getService().getId());
        response.setServiceName(order.getService().getName());
        response.setQuantity(order.getQuantity());
        response.setPrice(order.getPrice());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderTime(order.getOrderTime());
        if (order.getCreatedBy() != null) {
            response.setCreatedByUsername(order.getCreatedBy().getUsername());
        }
        return response;
    }
}

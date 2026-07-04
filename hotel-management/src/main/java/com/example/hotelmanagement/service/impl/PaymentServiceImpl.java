package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.PaymentRequest;
import com.example.hotelmanagement.dto.response.PaymentResponse;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.enums.InvoiceStatus;
import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.PaymentRepository;
import com.example.hotelmanagement.repository.UserRepository;
import com.example.hotelmanagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.VOID) {
            throw new IllegalStateException("Cannot process payment for a VOID invoice");
        }
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already fully PAID");
        }

        BigDecimal paymentAmount = request.getAmount();
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        if (paymentAmount.compareTo(invoice.getAmountDue()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds the amount due");
        }

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method");
        }
        
        User receivedBy = null;
        if (request.getReceivedById() != null) {
            receivedBy = userRepository.findById(request.getReceivedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .paymentMethod(method)
                .amount(paymentAmount)
                .paymentReference(request.getPaymentReference())
                .note(request.getNote())
                .receivedBy(receivedBy)
                .build();

        payment = paymentRepository.save(payment);

        // Update Invoice
        BigDecimal newAmountPaid = invoice.getAmountPaid() != null ? invoice.getAmountPaid().add(paymentAmount) : paymentAmount;
        BigDecimal newAmountDue = invoice.getAmountDue().subtract(paymentAmount);

        invoice.setAmountPaid(newAmountPaid);
        invoice.setAmountDue(newAmountDue);

        if (newAmountDue.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        return mapToResponse(payment, invoice);
    }

    @Override
    public List<PaymentResponse> getPaymentsByInvoiceId(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
                
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(payment -> mapToResponse(payment, invoice))
                .collect(Collectors.toList());
    }

    @Override
    public Page<PaymentResponse> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentTime").descending());
        return paymentRepository.findAll(pageable).map(payment -> mapToResponse(payment, payment.getInvoice()));
    }

    private PaymentResponse mapToResponse(Payment payment, Invoice invoice) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setInvoiceId(invoice.getId());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setPaymentAmount(payment.getAmount());
        response.setInvoiceStatus(invoice.getStatus().name());
        response.setAmountPaid(invoice.getAmountPaid());
        response.setAmountDue(invoice.getAmountDue());
        response.setPaymentTime(payment.getPaymentTime());
        response.setPaymentReference(payment.getPaymentReference());
        response.setNote(payment.getNote());
        if (payment.getReceivedBy() != null) {
            response.setReceivedByUsername(payment.getReceivedBy().getUsername());
        }
        return response;
    }
}

package com.yourwifi.payment.controller;

import com.yourwifi.common.exception.ApiException;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import com.yourwifi.payment.dto.CreatePaymentRequest;
import com.yourwifi.payment.dto.PaymentResponse;
import com.yourwifi.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomerRepository customerRepository;

    public PaymentController(PaymentService paymentService, CustomerRepository customerRepository) {
        this.paymentService = paymentService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request, Authentication authentication) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @PostMapping("/customer/payments")
    public ResponseEntity<PaymentResponse> createCustomerPayment(@Valid @RequestBody CreatePaymentRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        if (username == null || username.isBlank()) {
            throw new ApiException("UNAUTHENTICATED", "Authentication required.", HttpStatus.UNAUTHORIZED.value());
        }
        Customer customer = customerRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException("CUSTOMER_NOT_FOUND", "Customer not found.", HttpStatus.NOT_FOUND.value()));

        CreatePaymentRequest customerRequest = new CreatePaymentRequest(
            customer.getId(),
            request.packageId(),
            request.amount(),
            request.currency(),
            request.provider()
        );
        return ResponseEntity.ok(paymentService.createPayment(customerRequest));
    }
}
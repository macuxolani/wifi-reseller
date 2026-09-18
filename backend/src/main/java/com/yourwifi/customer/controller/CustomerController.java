package com.yourwifi.customer.controller;

import com.yourwifi.billing.service.BillingService;
import com.yourwifi.customer.dto.BalanceDto;
import com.yourwifi.common.exception.ApiException;
import com.yourwifi.customer.dto.CustomerDto;
import com.yourwifi.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/customers", "/api/customer"})
public class CustomerController {

    private final CustomerService customerService;
    private final BillingService billingService;

    public CustomerController(CustomerService customerService, BillingService billingService) {
        this.customerService = customerService;
        this.billingService = billingService;
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerDto> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ApiException("UNAUTHENTICATED", "Authentication required.", HttpStatus.UNAUTHORIZED.value());
        }
        return ResponseEntity.ok(customerService.getCurrentCustomer(authentication.getName()));
    }

    @GetMapping("/me/balance")
    public ResponseEntity<BalanceDto> balance(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ApiException("UNAUTHENTICATED", "Authentication required.", HttpStatus.UNAUTHORIZED.value());
        }
        var customer = customerService.getCurrentCustomer(authentication.getName());
        return ResponseEntity.ok(new BalanceDto(billingService.getRemainingMinutes(customer.id())));
    }
}

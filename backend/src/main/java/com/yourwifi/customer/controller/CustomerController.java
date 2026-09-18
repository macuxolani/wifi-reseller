package com.yourwifi.customer.controller;

import com.yourwifi.customer.dto.CustomerDto;
import com.yourwifi.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerDto> me() {
        return ResponseEntity.ok(customerService.getCurrentCustomer());
    }
}

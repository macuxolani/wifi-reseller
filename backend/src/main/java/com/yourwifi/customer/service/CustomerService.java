package com.yourwifi.customer.service;

import com.yourwifi.common.exception.ApiException;
import com.yourwifi.customer.dto.CustomerDto;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDto getCurrentCustomer(String username) {
        Customer customer = customerRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException("CUSTOMER_NOT_FOUND", "Customer profile not found.", HttpStatus.NOT_FOUND.value()));
        return new CustomerDto(
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getPhoneNumber(),
            customer.getEmail(),
            customer.getUsername(),
            customer.getStatus().name(),
            customer.getAccountCreatedAt(),
            customer.getLastLoginAt(),
            customer.getLastSeenAt()
        );
    }
}

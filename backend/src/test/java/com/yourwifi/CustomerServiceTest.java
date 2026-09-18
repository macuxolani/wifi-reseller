package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.yourwifi.common.enums.UserStatus;
import com.yourwifi.customer.dto.CustomerDto;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import com.yourwifi.customer.service.CustomerService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCurrentCustomerReturnsAuthenticatedCustomerProfile() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setUuid(UUID.randomUUID());
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setUsername("janedoe");
        customer.setEmail("jane@example.com");
        customer.setPhoneNumber("+27720000000");
        customer.setStatus(UserStatus.ACTIVE);
        customer.setAccountCreatedAt(Instant.now());
        customer.setLastLoginAt(Instant.now());
        customer.setLastSeenAt(Instant.now());

        when(customerRepository.findByUsername("janedoe")).thenReturn(Optional.of(customer));

        CustomerDto customerDto = customerService.getCurrentCustomer("janedoe");

        assertEquals("janedoe", customerDto.username());
        assertEquals("Jane", customerDto.firstName());
        assertEquals("Doe", customerDto.lastName());
    }
}

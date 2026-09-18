package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.yourwifi.auth.dto.AuthRequest;
import com.yourwifi.auth.dto.RegisterRequest;
import com.yourwifi.auth.service.AuthService;
import com.yourwifi.common.enums.UserStatus;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import com.yourwifi.security.jwt.JwtService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesCustomerAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest("Jane", "Doe", "janedoe", "jane@example.com", "+27720000000", "secret123");
        when(customerRepository.existsByUsername(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(jwtService.buildAuthResponse("janedoe")).thenReturn(new com.yourwifi.auth.dto.AuthResponse("access", "refresh", "Bearer", "janedoe"));

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("janedoe", response.username());
    }

    @Test
    void loginAuthenticatesExistingCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setUuid(UUID.randomUUID());
        customer.setUsername("janedoe");
        customer.setEmail("jane@example.com");
        customer.setPhoneNumber("+27720000000");
        customer.setPasswordHash("hashed-password");
        customer.setStatus(UserStatus.ACTIVE);
        customer.setAccountCreatedAt(Instant.now());

        when(customerRepository.findByUsername("janedoe")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("secret123", "hashed-password")).thenReturn(true);
        when(jwtService.buildAuthResponse("janedoe")).thenReturn(new com.yourwifi.auth.dto.AuthResponse("access", "refresh", "Bearer", "janedoe"));

        var response = authService.login(new AuthRequest("janedoe", "secret123"));

        assertNotNull(response);
        assertEquals("janedoe", response.username());
    }
}

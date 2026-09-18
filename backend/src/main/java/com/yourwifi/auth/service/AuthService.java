package com.yourwifi.auth.service;

import com.yourwifi.auth.dto.AuthRequest;
import com.yourwifi.auth.dto.AuthResponse;
import com.yourwifi.auth.dto.RegisterRequest;
import com.yourwifi.common.enums.UserStatus;
import com.yourwifi.admin.entity.AdminUser;
import com.yourwifi.admin.entity.Role;
import com.yourwifi.admin.repository.AdminUserRepository;
import com.yourwifi.common.exception.ApiException;
import com.yourwifi.customer.entity.Customer;
import com.yourwifi.customer.repository.CustomerRepository;
import com.yourwifi.security.jwt.JwtService;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CustomerRepository customerRepository, AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setUuid(UUID.randomUUID());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setUsername(request.username());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setPasswordHash(passwordEncoder.encode(request.password()));
        customer.setStatus(UserStatus.ACTIVE);
        customer.setAccountCreatedAt(Instant.now());
        customerRepository.save(customer);

        return jwtService.buildAuthResponse(customer.getUsername());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        Customer customer = customerRepository.findByUsername(request.username())
            .orElseThrow(() -> new ApiException("INVALID_CREDENTIALS", "Invalid credentials.", 401));

        if (!passwordEncoder.matches(request.password(), customer.getPasswordHash())) {
            throw new ApiException("INVALID_CREDENTIALS", "Invalid credentials.", 401);
        }

        customer.setLastLoginAt(Instant.now());
        customer.setLastSeenAt(Instant.now());
        customerRepository.save(customer);

        return jwtService.buildAuthResponse(customer.getUsername());
    }

    public AuthResponse refresh(String refreshToken) {
        String username = jwtService.validateRefreshToken(refreshToken);
        return jwtService.buildAuthResponse(username);
    }

    @Transactional(readOnly = true)
    public AuthResponse adminLogin(AuthRequest request) {
        AdminUser admin = adminUserRepository.findByUsername(request.username())
            .orElseThrow(() -> new ApiException("INVALID_CREDENTIALS", "Invalid credentials.", 401));
        if (admin.getStatus() != UserStatus.ACTIVE || !passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new ApiException("INVALID_CREDENTIALS", "Invalid credentials.", 401);
        }
        var roles = admin.getRoles().stream()
            .map(Role::getName)
            .map(Enum::name)
            .collect(Collectors.toSet());
        return jwtService.buildAuthResponse(admin.getUsername(), roles);
    }
}

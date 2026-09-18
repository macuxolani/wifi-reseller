package com.yourwifi.customer.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerDto(
    UUID id,
    String firstName,
    String lastName,
    String phoneNumber,
    String email,
    String username,
    String status,
    Instant accountCreatedAt,
    Instant lastLoginAt,
    Instant lastSeenAt
) {}

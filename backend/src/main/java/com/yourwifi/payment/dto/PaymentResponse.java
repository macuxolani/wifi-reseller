package com.yourwifi.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
    UUID id,
    String reference,
    UUID customerId,
    UUID packageId,
    BigDecimal amount,
    String currency,
    String provider,
    String status,
    Instant initiatedAt
) {}

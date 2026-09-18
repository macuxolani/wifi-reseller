package com.yourwifi.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
    UUID customerId,
    UUID packageId,
    BigDecimal amount,
    String currency,
    String provider
) {}

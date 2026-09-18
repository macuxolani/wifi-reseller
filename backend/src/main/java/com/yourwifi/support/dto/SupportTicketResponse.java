package com.yourwifi.support.dto;

import java.time.Instant;
import java.util.UUID;

public record SupportTicketResponse(
    UUID id,
    UUID customerId,
    String subject,
    String message,
    String status,
    String priority,
    Instant createdAt
) {}

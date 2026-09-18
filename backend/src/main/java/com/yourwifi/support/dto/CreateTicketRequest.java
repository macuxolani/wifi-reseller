package com.yourwifi.support.dto;

import java.util.UUID;

public record CreateTicketRequest(
    UUID customerId,
    String subject,
    String message,
    String priority
) {}

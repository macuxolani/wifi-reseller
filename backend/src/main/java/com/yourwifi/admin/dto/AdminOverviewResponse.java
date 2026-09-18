package com.yourwifi.admin.dto;

public record AdminOverviewResponse(
    long totalAdmins,
    long totalRoles,
    long totalAuditEvents
) {}

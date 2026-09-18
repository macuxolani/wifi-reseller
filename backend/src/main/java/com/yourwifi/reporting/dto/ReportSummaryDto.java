package com.yourwifi.reporting.dto;

import java.math.BigDecimal;

public record ReportSummaryDto(
    String periodLabel,
    long totalActiveSessions,
    long totalCustomers,
    long totalHotspots,
    long totalVoucherRedeemals,
    BigDecimal grossRevenue,
    BigDecimal netRevenue
) {}

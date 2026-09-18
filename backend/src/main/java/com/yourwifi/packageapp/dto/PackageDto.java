package com.yourwifi.packageapp.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PackageDto(
    UUID id,
    String name,
    Integer durationMinutes,
    Integer downloadSpeedMbps,
    Integer uploadSpeedMbps,
    BigDecimal price,
    String currency,
    String description,
    String status
) {}

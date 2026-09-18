package com.yourwifi.voucher.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateVoucherRequest(@NotNull UUID packageId, @NotNull Integer durationMinutes, BigDecimal price) {}
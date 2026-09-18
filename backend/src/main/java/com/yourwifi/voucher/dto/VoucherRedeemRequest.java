package com.yourwifi.voucher.dto;

import jakarta.validation.constraints.NotBlank;

public record VoucherRedeemRequest(
    @NotBlank String code,
    @NotBlank String username
) {}

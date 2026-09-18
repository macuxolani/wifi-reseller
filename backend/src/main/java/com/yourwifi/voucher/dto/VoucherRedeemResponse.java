package com.yourwifi.voucher.dto;

public record VoucherRedeemResponse(String code, String status, int minutesAdded, String message) {}

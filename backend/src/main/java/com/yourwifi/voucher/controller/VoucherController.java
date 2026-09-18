package com.yourwifi.voucher.controller;

import com.yourwifi.voucher.dto.VoucherRedeemRequest;
import com.yourwifi.voucher.dto.VoucherRedeemResponse;
import com.yourwifi.voucher.dto.CreateVoucherRequest;
import com.yourwifi.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping("/vouchers/redeem")
    public ResponseEntity<VoucherRedeemResponse> redeem(@Valid @RequestBody VoucherRedeemRequest request) {
        return ResponseEntity.ok(voucherService.redeemVoucher(request));
    }

    @PostMapping("/vouchers")
    public ResponseEntity<com.yourwifi.voucher.entity.Voucher> create(@Valid @RequestBody CreateVoucherRequest request, Authentication authentication) {
        return ResponseEntity.ok(voucherService.createVoucher(request, authentication.getName()));
    }
}
